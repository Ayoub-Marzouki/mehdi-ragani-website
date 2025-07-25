package art.mehdiragani.mehdiragani.public_.controllers;

import java.util.List;
import java.util.ArrayList;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.services.ArtworkService;
import art.mehdiragani.mehdiragani.core.services.PrintService;
import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.store.dto.ProductDTO;
import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {
    private final ArtworkService artworkService;
    private final CartService cartService;
    private final PrintService printService;

    public HomeController(ArtworkService artworkService, CartService cartService, PrintService printService) {
        this.artworkService = artworkService;
        this.cartService = cartService;
        this.printService = printService;
    }
    
    @GetMapping
    public String index(Model model, HttpSession session, Authentication authentication) {
        List<Artwork> artworks = artworkService.getAllArtworks();
        List<Print> prints = printService.getAllPrints();
        // Combine artworks and prints into a single products list
        List<ProductDTO> products = new ArrayList<>();
        for (Artwork a : artworks) {
            products.add(new ProductDTO("ARTWORK", a.getId(), a.getTitle(), a.getMainImagePath(), a.getPrice(), null, a.getStatus() != null ? a.getStatus().name() : null, a.getYear()));
        }
        for (Print p : prints) {
            products.add(new ProductDTO("PRINT", p.getId(), p.getTitle(), p.getMainImagePath(), null, p.getBasePrice(), null, null));
        }
        model.addAttribute("products", products);
        // Add cart data for header
        Cart cart = cartService.getOrCreateCart(session, authentication);
        model.addAttribute("cart", cart);
        return "index";
    }
}
