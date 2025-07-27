package art.mehdiragani.mehdiragani.store.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import art.mehdiragani.mehdiragani.store.services.CartService;
import art.mehdiragani.mehdiragani.payment.services.OrderService;
import art.mehdiragani.mehdiragani.payment.models.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import art.mehdiragani.mehdiragani.payment.repositories.OrderRepository;

@Component
public class CartAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final CartService cartService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    
    public CartAuthenticationSuccessHandler(CartService cartService, OrderService orderService, OrderRepository orderRepository, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        // Get the pre-auth session ID BEFORE the session changes
        HttpSession session = request.getSession(false);
        String preAuthSessionId = null;
        if (session != null) {
            preAuthSessionId = (String) session.getAttribute("PRE_AUTH_SESSION_ID");
        }
        
        // Get the actual User object from our service
        User user = userService.getUserByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return;
        }
        
        // Merge carts first
        cartService.mergeCarts(request.getSession(), user);
        
        // Merge orders using the pre-auth session ID if available
        if (preAuthSessionId != null) {
            List<Order> guestOrders = orderService.findGuestOrdersBySessionId(preAuthSessionId);
            
            if (!guestOrders.isEmpty()) {
                // Update each order to belong to the user
                for (Order order : guestOrders) {
                    order.setUser(user);
                    orderRepository.save(order);
                }
            }
        } else {
            // Fallback to current session ID
            orderService.mergeOrders(request.getSession(), user);
        }
        
        // Handle redirect
        String redirectTo = request.getParameter("redirectTo");
        if (redirectTo == null || redirectTo.isEmpty()) {
            // Check session for post-registration redirect
            HttpSession currentSession = request.getSession(false);
            if (currentSession != null) {
                redirectTo = (String) currentSession.getAttribute("POST_REGISTRATION_REDIRECT");
                if (redirectTo != null) {
                    // Remove from session after use
                    currentSession.removeAttribute("POST_REGISTRATION_REDIRECT");
                }
            }
        }
        
        if (redirectTo != null && !redirectTo.isEmpty()) {
            response.sendRedirect(redirectTo);
        } else {
            response.sendRedirect("/");
        }
    }
}