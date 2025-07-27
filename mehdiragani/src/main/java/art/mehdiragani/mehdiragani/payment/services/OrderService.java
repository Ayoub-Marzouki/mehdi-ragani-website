package art.mehdiragani.mehdiragani.payment.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import art.mehdiragani.mehdiragani.payment.enums.OrderStatus;
import art.mehdiragani.mehdiragani.payment.enums.PaymentStatus;
import art.mehdiragani.mehdiragani.payment.models.Order;
import art.mehdiragani.mehdiragani.payment.models.OrderItem;
import art.mehdiragani.mehdiragani.payment.repositories.OrderRepository;
import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.services.PrintService;
import art.mehdiragani.mehdiragani.core.services.ArtworkService;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus;
import org.hibernate.Hibernate;
import art.mehdiragani.mehdiragani.core.models.Artwork;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    // Can use service instead of repo here, no circular dependency (OrderService doesn't depend on user or cart services)
    private final CartService cartService;
    private final UserService userService;
    private final PrintService printService;
    private final ArtworkService artworkService;

    
    public OrderService(OrderRepository orderRepository, CartService cartService, UserService userService, PrintService printService, ArtworkService artworkService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
        this.printService = printService;
        this.artworkService = artworkService;
    }

    public Optional<Order> getOrder(UUID id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getOrderWithItems(UUID id) {
        return orderRepository.findByIdWithItems(id);
    }

    /**
     * Snapshots the current cart into a new Order, persists it, and then clears the cart.
     * This is used for traditional (non-PayPal) order placement, where the order is finalized immediately.
     * Artworks are marked as sold, print stock is decremented, and the cart is emptied.
     *
     * @param session HTTP session for guest identification
     * @param auth    Spring Security authentication for user identification
     * @return the saved Order
     */
    public Order placeOrder(HttpSession session, Authentication auth) {
        User user = userService.currentUser(auth); // null for guests
        Cart cart = cartService.getOrCreateCart(session, auth);

        Order order = new Order();
        order.setUser(user);
        order.setTotal(cart.getTotalPrice());

        // Artworks
        cart.getItems().forEach(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setArtwork(ci.getArtwork());
            oi.setQuantity(ci.getQuantity());
            oi.setLineTotal(ci.getLineTotal());
            order.getItems().add(oi);
            // Mark artwork as sold
            ci.getArtwork().setStatus(ArtworkStatus.SOLD);
            artworkService.updateArtwork(ci.getArtwork());
        });

        // Prints
        cart.getPrintItems().forEach(pi -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setPrint(pi.getPrint());
            oi.setPrintType(pi.getType());
            oi.setPrintSize(pi.getSize());
            oi.setFraming(pi.getFraming());
            oi.setQuantity(pi.getQuantity());
            oi.setLineTotal(BigDecimal.valueOf(pi.getLineTotal()));
            order.getItems().add(oi);
            // Decrement print stock if limited
            Print print = pi.getPrint();
            if (print.getStock() != null) {
                int newStock = print.getStock() - pi.getQuantity();
                print.setStock(Math.max(0, newStock));
                printService.savePrint(print);
            }
        });

        Order saved = orderRepository.save(order);
        cart.getItems().clear();
        cart.getPrintItems().clear();
        cartService.saveCart(cart);
        return saved;
    }

    /**
     * List all orders for the given user.
     */
    public List<Order> listUserOrders(User user) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    /**
     * Find guest orders by session ID (for order merging)
     */
    public List<Order> findGuestOrdersBySessionId(String sessionId) {
        return orderRepository.findBySessionIdOrderByCreatedAtDesc(sessionId);
    }

    /**
     * Merges guest orders into a user's account upon registration/login.
     * Similar to cart merging, but for completed orders.
     *
     * @param session HTTP session containing the guest session ID
     * @param user    authenticated user to merge orders into
     */
    public void mergeOrders(HttpSession session, User user) {
        String sessionId = session.getId();
        String preAuthSessionId = (String) session.getAttribute("PRE_AUTH_SESSION_ID");
        
        System.out.println("=== ORDER MERGING DEBUG ===");
        System.out.println("Current Session ID: " + sessionId);
        System.out.println("Pre-Auth Session ID: " + preAuthSessionId);
        System.out.println("User: " + user.getUsername());
        
        // Try to find guest orders with pre-authentication session ID first
        String sessionIdToUse = preAuthSessionId != null ? preAuthSessionId : sessionId;
        List<Order> guestOrders = findGuestOrdersBySessionId(sessionIdToUse);
        
        System.out.println("Found " + guestOrders.size() + " guest orders for session ID: " + sessionIdToUse);
        
        for (Order guestOrder : guestOrders) {
            System.out.println("Merging order: " + guestOrder.getId() + " (Total: " + guestOrder.getTotal() + ")");
            // Update the order to belong to the user
            guestOrder.setUser(user);
            orderRepository.save(guestOrder);
            System.out.println("Order merged successfully");
        }
        
        System.out.println("=== END ORDER MERGING DEBUG ===");
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public void deleteOrderById(UUID id) {
        orderRepository.deleteById(id);
    }


    /**
     * Creates a pending Order from the current cart, but does NOT finalize it or clear the cart.
     * This is used for PayPal/online payment flows, where the order is only finalized after payment capture.
     * The order is saved with status Pending, and payment status Pending.
     *
     * @param cart the current Cart
     * @param session HTTP session for guest identification
     * @param auth Spring Security authentication for user identification
     * @return the saved pending Order
     */
    public Order createPendingOrder(Cart cart, HttpSession session, Authentication auth) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotal(cart.getTotalPrice());
        order.setCurrency("EUR");  // PayPal supported currency

        // Set sessionId for guest orders
        if (cart.getUser() == null) {
            order.setSessionId(session.getId());
        }

        // Artworks
        cart.getItems().forEach(cartItem -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setArtwork(cartItem.getArtwork());
            oi.setQuantity(cartItem.getQuantity());
            oi.setLineTotal(cartItem.getLineTotal());
            order.getItems().add(oi);
        });

        // Prints
        cart.getPrintItems().forEach(pi -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setPrint(pi.getPrint());
            oi.setPrintType(pi.getType());
            oi.setPrintSize(pi.getSize());
            oi.setFraming(pi.getFraming());
            oi.setQuantity(pi.getQuantity());
            oi.setLineTotal(BigDecimal.valueOf(pi.getLineTotal()));
            order.getItems().add(oi);
        });
        
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }
    
    /**
     * Updates the payment status and reference for an order.
     * Used to record the PayPal order ID or other payment reference after order creation.
     *
     * @param orderId          the order's UUID
     * @param status           the new payment status
     * @param paymentReference the payment reference (e.g., PayPal order ID)
     */
    public void updatePaymentStatus(UUID orderId, PaymentStatus status, String paymentReference) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPaymentStatus(status);
            order.setPaymentReference(paymentReference);
            orderRepository.save(order);
        });
    }
    
    /**
     * Completes an order after successful payment capture.
     * Updates order/payment status, marks artworks as sold, decrements print stock, and clears the cart.
     *
     * @param orderId         the order's UUID
     * @param paymentReference the payment capture reference (e.g., PayPal capture ID)
     * @param session         HTTP session for guest identification
     * @param auth            Spring Security authentication for user identification
     */
    public void completeOrder(UUID orderId, String paymentReference, HttpSession session, Authentication auth) {
        orderRepository.findById(orderId).ifPresent(order -> {
            // Update order status
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.setOrderStatus(OrderStatus.CONFIRMED);
            order.setPaymentReference(paymentReference);
            orderRepository.save(order);
            // Mark artworks as sold and decrement print stock
            for (OrderItem oi : order.getItems()) {
                if (oi.getArtwork() != null) {
                    UUID artworkId = oi.getArtwork().getId();
                    // Fetch a fully populated entity
                    var freshArtwork = artworkService.getArtworkById(artworkId);
                    freshArtwork = (Artwork) Hibernate.unproxy(freshArtwork);
                    // Defensive check
                    if (freshArtwork.getTitle() == null || freshArtwork.getTitle().isBlank() ||
                        freshArtwork.getMainImagePath() == null || freshArtwork.getMainImagePath().isBlank() ||
                        freshArtwork.getYear() < 0) {
                        throw new IllegalStateException("Artwork data is incomplete: " + freshArtwork);
                    }
                    freshArtwork.setStatus(ArtworkStatus.SOLD);
                    artworkService.updateArtwork(freshArtwork);
                }
                if (oi.getPrint() != null && oi.getPrint().getStock() != null) {
                    int newStock = oi.getPrint().getStock() - oi.getQuantity();
                    oi.getPrint().setStock(Math.max(0, newStock));
                    printService.savePrint(oi.getPrint());
                }
            }
            // Clear cart
            Cart cart = cartService.getOrCreateCart(session, auth);
            cart.getItems().clear();
            cart.getPrintItems().clear();
            cartService.saveCart(cart);
        });
    }
}
