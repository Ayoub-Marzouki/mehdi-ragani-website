package art.mehdiragani.mehdiragani.store.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import art.mehdiragani.mehdiragani.core.services.ArtworkService;

@Controller
@RequestMapping("/store")
public class StoreController {
    private final ArtworkService artworkService;

    @Autowired
    public StoreController(ArtworkService artworkService) {
        this.artworkService = artworkService;
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
    @RequestParam(name = "status", required = false) ArtworkStatus status
    ) {
        
        List<Artwork> artworks = artworkService.findByCriteria(
            minPrice, maxPrice, minWidth, maxWidth, minHeight, maxHeight, theme, feel, status
        );

        // Follow Pinterest Style in the view by dividing artworks into 3 parallel vertical sections
        List<List<Artwork>> columns = splitIntoColumns(artworks, 3);

        model.addAttribute("columns", columns);

        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        model.addAttribute("minWidth", minWidth);
        model.addAttribute("minHeight", minHeight);
        
        model.addAttribute("maxWidth", maxWidth);
        model.addAttribute("maxHeight", maxHeight);

        model.addAttribute("theme", theme);
        model.addAttribute("feel", feel);
        model.addAttribute("status", status);

        model.addAttribute("allThemes", List.of(ArtworkTheme.values()));
        model.addAttribute("allFeels",  List.of(ArtworkFeel.values()));
        model.addAttribute("allStatus", List.of(ArtworkStatus.values()));
        
        return "store/store";
    }

    @GetMapping("/{id}")
    public String artworkDetails (Model model, @PathVariable("id") UUID id) {
        
        Artwork artwork = artworkService.getArtworkById(id);

        model.addAttribute("artwork", artwork);

        return "store/artwork-details";
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
