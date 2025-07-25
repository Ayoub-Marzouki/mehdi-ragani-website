package art.mehdiragani.mehdiragani.store.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.auth.services.UserService;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CartAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final CartService cartService;
    private final UserService userService;

    public CartAuthenticationSuccessHandler(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = userService.getUserByUsername(authentication.getName()).get();
        cartService.mergeCarts(request.getSession(), user);
        
        response.sendRedirect("/");
    }
}