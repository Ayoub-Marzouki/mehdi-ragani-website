package art.mehdiragani.mehdiragani.store.services;

import java.util.UUID;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.repositories.UserRepository;
import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.repositories.ArtworkRepository;
import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.models.enums.PrintType;
import art.mehdiragani.mehdiragani.core.services.PrintService;
import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.repositories.CartRepository;
import art.mehdiragani.mehdiragani.store.models.PrintCartItem;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    
    // We used repositories and not services to avoid circular dependencies
    private final ArtworkRepository artworkRepository;
    private final UserRepository userRepository;
    private final PrintService printService;

    public CartService(CartRepository cartRepository, ArtworkRepository artworkRepository, UserRepository userRepository, PrintService printService) {
        this.cartRepository = cartRepository;
        this.artworkRepository = artworkRepository;
        this.userRepository = userRepository;
        this.printService = printService;
    }

     /**
     * Retrieves an existing cart for the authenticated user,
     * or creates and persists a new one if none exists.
     *
     * @param session        HTTP session for guest identification
     * @param authentication Spring Security authentication to identify user
     * @return the found or newly created Cart
     */
    public Cart getOrCreateCart(HttpSession session, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return getOrCreateUserCart(authentication);
        } else {
            return getOrCreateGuestCart(session);
        }
    }

    /**
     * Fetches or creates a cart tied to a logged-in user.
     *
     * @param auth Spring Security authentication containing the username
     * @return the existing or newly created Cart for that user
     * @throws EntityNotFoundException if the user cannot be found
     */
    private Cart getOrCreateUserCart(Authentication auth) {
        String username = auth.getName();
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        return cartRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                Cart c = new Cart();
                c.setUser(user);
                return cartRepository.save(c);
            });
    }
    
    /**
     * Fetches or creates a cart tied to an anonymous guest,
     * using the HTTP session ID.
     *
     * @param session HTTP session used to identify the guest
     * @return the existing or newly created guest Cart
     */
    private Cart getOrCreateGuestCart(HttpSession session) {
        String sessionId = session.getId();
        return cartRepository.findBySessionId(sessionId)
            .orElseGet(() -> {
                Cart newCart = new Cart();
                newCart.setSessionId(sessionId);
                return cartRepository.save(newCart);
            });
    }

    /**
     * Adds an artwork to the user's or guest's cart, creating the cart if necessary.
     *
     * @param artworkId    ID of the artwork to add
     * @param quantity     quantity to add (must be positive)
     * @param session      HTTP session for guest identification
     * @param authentication Spring Security authentication for user identification
     * @throws EntityNotFoundException if the artwork does not exist
     * @throws IllegalStateException if the artwork is already in cart
     */
    public void addToCart(UUID artworkId, int quantity, 
                         HttpSession session, Authentication auth) {
        Cart cart = getOrCreateCart(session, auth);
        Artwork artwork = artworkRepository.findById(artworkId)
            .orElseThrow(() -> new EntityNotFoundException("Artwork not found"));
        
        try {
        cart.addItem(artwork, quantity);
        cartRepository.save(cart);
        } catch (IllegalStateException e) {
            // Artwork is already in cart - this is expected behavior for unique items
            // We can either ignore it or re-throw it depending on desired UX
            throw e; // Re-throw to inform the user
        }
    }
    
    /**
     * Adds a print to the user's or guest's cart, creating the cart if necessary.
     * Calculates price based on options.
     */
    public void addPrintToCart(UUID printId, PrintType type, PrintSize size, Framing framing, int quantity, HttpSession session, Authentication auth) {
        Cart cart = getOrCreateCart(session, auth);
        Print print = printService.getPrintById(printId);
        // Price calculation logic (sensible defaults, can be adjusted)
        double price = print.getBasePrice();
        // Example multipliers (customize as needed)
        switch (size) {
            case LARGE_60x80: price *= 1.5; break;
            case LARGE_50x70: price *= 1.35; break;
            case MEDIUM_40x40: price *= 1.15; break;
            case MEDIUM_30x40: price *= 1.10; break;
            case SMALL_20x20: price *= 1.0; break;
            default: price *= 1.0; break;
        }
        switch (type) {
            case CANVAS: price += 100; break;
            case FINE_ART_PAPER: price += 50; break;
            case PHOTO_PAPER: price += 30; break;
            case VINYL: price += 20; break;
        }
        switch (framing) {
            case BLACK: price += 80; break;
            case WHITE: price += 80; break;
            case NONE: break;
        }
        PrintCartItem item = new PrintCartItem();
        item.setPrint(print);
        item.setType(type);
        item.setSize(size);
        item.setFraming(framing);
        item.setQuantity(quantity);
        item.setUnitPrice(price);
        item.calculateLineTotal();
        item.setCart(cart);
        cart.getPrintItems().add(item);
        cartRepository.save(cart);
    }

    /**
     * Merges items from an anonymous guest cart into a user's cart upon login,
     * then deletes the guest cart.
     *
     * @param session HTTP session containing the guest cart
     * @param user    authenticated user into whose cart items are merged
     */
    public void mergeCarts(HttpSession session, User user) {
        String sessionId = session.getId();
        cartRepository.findBySessionId(sessionId).ifPresent(guestCart -> {
            Cart userCart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
            
            // Transfer items
            guestCart.getItems().forEach(item -> 
                userCart.addItem(item.getArtwork(), item.getQuantity())
            );
            
            cartRepository.save(userCart);
            cartRepository.delete(guestCart);
        });
    }

    /**
     * Removes a specific artwork from the cart.
     *
     * @param artworkId      ID of the artwork to remove
     * @param session        HTTP session for guest identification
     * @param authentication Spring Security authentication for user identification
     */
    public void removeFromCart(UUID artworkId, HttpSession session, Authentication authentication) {
        Cart cart = getOrCreateCart(session, authentication);
        cart.removeItem(artworkId); // This happens only in memory
        cartRepository.save(cart); // That's why we save the cart again; to save the update
    }

    /**
     * Updates the quantity for a specific artwork in the cart.
     *
     * @param artworkId      ID of the artwork whose quantity is updated
     * @param newQuantity    new quantity (must be positive)
     * @param session        HTTP session for guest identification
     * @param authentication Spring Security authentication for user identification
     */
    public void updateQuantity(UUID artworkId, int newQuantity, HttpSession session, Authentication authentication) {
        Cart cart = getOrCreateCart(session, authentication);
        cart.updateQuantity(artworkId, newQuantity);
        cartRepository.save(cart);
    }

    public void removePrintFromCart(UUID printCartItemId, HttpSession session, Authentication authentication) {
        Cart cart = getOrCreateCart(session, authentication);
        boolean removed = cart.getPrintItems().removeIf(item -> item.getId().equals(printCartItemId));
        if (removed) {
            cartRepository.save(cart);
        }
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }
    public Cart getCartById(UUID id) {
        return cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cart not found"));
    }
    public void deleteCartById(UUID id) {
        cartRepository.deleteById(id);
    }
}