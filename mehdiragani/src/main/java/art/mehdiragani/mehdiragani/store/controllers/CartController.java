package art.mehdiragani.mehdiragani.store.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(HttpSession session, Authentication authentication, Model model) {
        Cart cart = cartService.getOrCreateCart(session, authentication);
        model.addAttribute("cart", cart);
        return "store/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("artworkId") UUID artworkId, @RequestParam(value = "quantity", defaultValue = "1") int quantity, HttpSession session, Authentication authentication) {
        cartService.addToCart(artworkId, quantity, session, authentication);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{artworkId}")
    public String removeFromCart(@PathVariable("artworkId") UUID artworkId,
                                 HttpSession session,
                                 Authentication authentication) {
        cartService.removeFromCart(artworkId, session, authentication);
        return "redirect:/cart";
    }

    // @PostMapping("/update/{artworkId}")
    // public String updateQuantity(@PathVariable("artworkId") UUID artworkId,
    //                             @RequestParam("quantity") int newQuantity,
    //                             HttpSession session,
    //                             Authentication authentication) {
    //     cartService.updateQuantity(artworkId, newQuantity, session, authentication);
    //     return "redirect:/store/cart";
    // }
}