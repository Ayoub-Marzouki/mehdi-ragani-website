package art.mehdiragani.mehdiragani.admin.controllers;

import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/carts")
public class AdminCartController {
    private final CartService cartService;

    public AdminCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String listCarts(Model model) {
        List<Cart> carts = cartService.getAllCarts();
        model.addAttribute("carts", carts);
        return "admin/carts";
    }

    @GetMapping("/{id}")
    public String cartDetails(@PathVariable("id") UUID id, Model model) {
        Cart cart = cartService.getCartById(id);
        model.addAttribute("cart", cart);
        return "admin/cart-details";
    }
} 