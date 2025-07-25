package art.mehdiragani.mehdiragani.store.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.models.enums.PrintType;

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
    public String addToCart(@RequestParam("artworkId") UUID artworkId, @RequestParam(value = "quantity", defaultValue = "1") int quantity, HttpSession session, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(artworkId, quantity, session, authentication);
            return "redirect:/cart";
        } catch (IllegalStateException e) {
            String errorMsg = e.getMessage().contains("sold") ?
                "This artwork has already been " + art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus.SOLD.getDisplayName().toLowerCase() + " and cannot be added to your cart." :
                "This artwork is already in your cart. Artworks are unique items.";
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/cart";
        }
    }

    @PostMapping("/add-print")
    public String addPrintToCart(@RequestParam("printId") UUID printId,
                                 @RequestParam("type") PrintType type,
                                 @RequestParam("size") PrintSize size,
                                 @RequestParam("framing") Framing framing,
                                 @RequestParam("quantity") int quantity,
                                 HttpSession session,
                                 Authentication authentication) {
        cartService.addPrintToCart(printId, type, size, framing, quantity, session, authentication);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{artworkId}")
    public String removeFromCart(@PathVariable("artworkId") UUID artworkId,
                                 HttpSession session,
                                 Authentication authentication) {
        cartService.removeFromCart(artworkId, session, authentication);
        return "redirect:/cart";
    }

    @PostMapping("/remove-print/{id}")
    public String removePrintFromCart(@PathVariable("id") UUID printCartItemId,
                                      HttpSession session,
                                      Authentication authentication) {
        cartService.removePrintFromCart(printCartItemId, session, authentication);
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