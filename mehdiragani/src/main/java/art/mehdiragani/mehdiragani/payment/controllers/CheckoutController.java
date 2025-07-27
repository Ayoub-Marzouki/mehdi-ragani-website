package art.mehdiragani.mehdiragani.payment.controllers;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.payment.config.PayPalConfig;

import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    private final CartService cartService;
    private final PayPalConfig payPalConfig; 

    public CheckoutController(CartService cartService, PayPalConfig payPalConfig) {
        this.cartService = cartService;
        this.payPalConfig = payPalConfig;
    }

    @GetMapping
    public String checkoutView(Model model, HttpSession session, Authentication auth) {
        Cart cart = cartService.getOrCreateCart(session, auth);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotalPrice());
        model.addAttribute("paypalClientId", payPalConfig.getId());
        model.addAttribute("currency","EUR");

        return "payment/checkout";
    }
}
