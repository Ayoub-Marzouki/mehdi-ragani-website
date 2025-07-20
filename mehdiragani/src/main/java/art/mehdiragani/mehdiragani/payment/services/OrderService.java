package art.mehdiragani.mehdiragani.payment.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    // Can use service instead of repo here, no circular dependency (OrderService doesn't depend on user or cart services)
    private final CartService cartService;
    private final UserService userService;

    
    public OrderService(OrderRepository orderRepository, CartService cartService, UserService userService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.userService = userService;
    }

    public Optional<Order> getOrder(UUID id) {
        return orderRepository.findById(id);
    }

    /** 
     * Snapshot the cart into a new Order, persist it, then clear the cart. 
     */
    public Order placeOrder(HttpSession session, Authentication auth) {
        User user = userService.currentUser(auth); // null for guests
        Cart cart = cartService.getOrCreateCart(session, auth);

        Order order = new Order();
        order.setUser(user);
        order.setTotal(cart.getTotalPrice());
        cart.getItems().forEach(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setArtwork(ci.getArtwork());
            oi.setQuantity(ci.getQuantity());
            oi.setLineTotal(ci.getLineTotal());
            order.getItems().add(oi);
        });

        Order saved = orderRepository.save(order);
        cart.getItems().clear();
        cartService.saveCart(cart);
        return saved;
    }

    /**
     * List all orders for the given user.
     */
    public List<Order> listUserOrders(User user) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }


    public Order createPendingOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotal(cart.getTotalPrice());
        order.setCurrency("USD");  // PayPal supported currency
        
        // Map cart items to order items
        cart.getItems().forEach(cartItem -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setArtwork(cartItem.getArtwork());
            oi.setQuantity(cartItem.getQuantity());
            oi.setLineTotal(cartItem.getLineTotal());
            order.getItems().add(oi);
        });
        
        order.setPaymentStatus(PaymentStatus.Pending);
        order.setOrderStatus(OrderStatus.Pending);
        
        return orderRepository.save(order);
    }
    
    public void updatePaymentStatus(UUID orderId, PaymentStatus status, String paymentReference) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPaymentStatus(status);
            order.setPaymentReference(paymentReference);
            orderRepository.save(order);
        });
    }
    
    public void completeOrder(UUID orderId, String paymentReference, HttpSession session, Authentication auth) {
        orderRepository.findById(orderId).ifPresent(order -> {
            // Update order status
            order.setPaymentStatus(PaymentStatus.Completed);
            order.setOrderStatus(OrderStatus.Confirmed);
            order.setPaymentReference(paymentReference);
            orderRepository.save(order);
            
            // Clear cart
            Cart cart = cartService.getOrCreateCart(session, auth);
            cart.getItems().clear();
            cartService.saveCart(cart);
        });
    }
}
