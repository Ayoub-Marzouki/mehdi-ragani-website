package art.mehdiragani.mehdiragani.store.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import art.mehdiragani.mehdiragani.core.models.enums.PrintType;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.services.PrintService;
import art.mehdiragani.mehdiragani.core.services.ArtworkService;
import art.mehdiragani.mehdiragani.store.dto.ProductDTO;
import art.mehdiragani.mehdiragani.store.models.Cart;
import art.mehdiragani.mehdiragani.store.services.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfToken;

@Controller
@RequestMapping("/store")
public class StoreController {
    private final ArtworkService artworkService;
    private final CartService cartService;
    private final PrintService printService;

    public StoreController(ArtworkService artworkService, CartService cartService, PrintService printService) {
        this.artworkService = artworkService;
        this.cartService = cartService;
        this.printService = printService;
    }
    
    @GetMapping
    public String store(Model model,  
    @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
    @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
    @RequestParam(name = "minWidth", required = false) Double minWidth,
    @RequestParam(name = "minHeight", required = false) Double minHeight,
    @RequestParam(name = "maxWidth", required = false) Double maxWidth,
    @RequestParam(name = "maxHeight", required = false) Double maxHeight,
    @RequestParam(name = "theme", required = false) ArtworkTheme theme,
    @RequestParam(name = "feel", required = false) ArtworkFeel feel,
    @RequestParam(name = "status", required = false) ArtworkStatus status,
    @RequestParam(name = "type", required = false, defaultValue = "ALL") String type
    ) {
        List<Artwork> artworks = artworkService.findByCriteria(
            minPrice, maxPrice, minWidth, maxWidth, minHeight, maxHeight, theme, feel, status
        );

        List<Print> filteredPrints = printService.findByCriteria(
            minPrice, maxPrice, minWidth, maxWidth, minHeight, maxHeight, theme, feel
        );

        // Combine and filter by type using ProductDTO
        List<ProductDTO> combined = new ArrayList<>();
        if (type.equals("ALL") || type.isEmpty()) {
            for (Artwork a : artworks) {
                combined.add(new ProductDTO(
                    "ARTWORK",
                    a.getId(),
                    a.getTitle(),
                    a.getMainImagePath(),
                    a.getPrice(),
                    null,
                    a.getStatus() != null ? a.getStatus().name() : null,
                    a.getStatus() != null ? a.getStatus().getDisplayName() : null,
                    a.getYear()
                ));
            }
            for (Print p : filteredPrints) {
                combined.add(new ProductDTO("PRINT", p.getId(), p.getTitle(), p.getMainImagePath(), null, p.getBasePrice(), null, null, null));
            }
        } else if (type.equals("ORIGINAL")) {
            for (Artwork a : artworks) {
                combined.add(new ProductDTO(
                    "ARTWORK",
                    a.getId(),
                    a.getTitle(),
                    a.getMainImagePath(),
                    a.getPrice(),
                    null,
                    a.getStatus() != null ? a.getStatus().name() : null,
                    a.getStatus() != null ? a.getStatus().getDisplayName() : null,
                    a.getYear()
                ));
            }
        } else if (type.equals("PRINT")) {
            for (Print p : filteredPrints) {
                combined.add(new ProductDTO("PRINT", p.getId(), p.getTitle(), p.getMainImagePath(), null, p.getBasePrice(), null, null, null));
            }
        }
        // Sort by title for consistency
        combined.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        // Pinterest-style columns
        List<List<ProductDTO>> columns = splitIntoColumns(combined, 3);
        model.addAttribute("columns", columns);
        model.addAttribute("artworks", combined); // for phone
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minWidth", minWidth);
        model.addAttribute("minHeight", minHeight);
        model.addAttribute("maxWidth", maxWidth);
        model.addAttribute("maxHeight", maxHeight);
        model.addAttribute("theme", theme);
        model.addAttribute("feel", feel);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        model.addAttribute("allThemes", List.of(ArtworkTheme.values()));
        model.addAttribute("allFeels",  List.of(ArtworkFeel.values()));
        model.addAttribute("allStatus", List.of(ArtworkStatus.values()));
        return "store/store";
    }

    @GetMapping("/{id}")
    public String artworkDetails (Model model, @PathVariable("id") UUID id, HttpSession session, Authentication authentication) {
        
        Artwork artwork = artworkService.getArtworkById(id);

        model.addAttribute("artwork", artwork);
        // Add cart data for header
        Cart cart = cartService.getOrCreateCart(session, authentication);
        model.addAttribute("cart", cart);

        return "store/artwork-details";
    }

    @GetMapping("/print/{id}")
    public String printDetails(Model model, @PathVariable("id") UUID id, CsrfToken csrfToken) {
        Print print = printService.getPrintById(id);
        
        // Get compatible print sizes based on artwork aspect ratio
        List<PrintSize> compatibleSizes = printService.getCompatiblePrintSizes(print.getAspectRatio());
        
        model.addAttribute("print", print);
        model.addAttribute("printSizes", compatibleSizes);
        model.addAttribute("printTypes", PrintType.values());
        model.addAttribute("framings", Framing.values());
        model.addAttribute("_csrf", csrfToken);
        return "store/print-details";
    }

    @PostMapping("/print/price")
    @ResponseBody
    public String calculatePrintPrice(@RequestParam("printId") UUID printId,
                                      @RequestParam("type") PrintType type,
                                      @RequestParam("size") PrintSize size,
                                      @RequestParam("framing") Framing framing,
                                      @RequestParam("quantity") int quantity) {
        
        Print print = printService.getPrintById(printId);
        double totalPrice = printService.calculatePrintPrice(print, size, type, framing, quantity);
        return String.format("%.2f EUR", totalPrice);
    }

    // Helper methods

   /**
    * Partition a list into `columns`; sublists, roughly equal in size.
    * Usage example : Store view (Pinterest website style)
    * @param <T>      element type
    * @param list     the original list
    * @param columns  number of sublists to produce
    * @return         a List of `columns` sublists (the last ones may be empty if list.size() < columns)
    */
    private <T> List<List<T>> splitIntoColumns(List<T> list, int numColumns) {
        List<List<T>> columns = new ArrayList<>();
        int size = list.size();
        int baseSize = size / numColumns; // Minimum items per column
        int remainder = size % numColumns; // Extra items to distribute
    
        int start = 0;
        for (int i = 0; i < numColumns; i++) {
            int extra = (i < remainder) ? 1 : 0; // Add 1 item if this column gets an extra
            int end = start + baseSize + extra;
            columns.add(list.subList(start, Math.min(end, size)));
            start = end;
        }
        return columns;
    }
}
