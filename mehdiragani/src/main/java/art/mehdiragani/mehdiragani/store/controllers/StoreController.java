package art.mehdiragani.mehdiragani.store.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.core.models.Artwork;
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
    public String store(Model model) {
        List<Artwork> artworks = artworkService.getAllArtworks();

        // Follow Pinterest Style in the view by dividing artworks into 3 parallel vertical sections
        List<List<Artwork>> columns = splitIntoColumns(artworks, 3);

        model.addAttribute("columns", columns);
        
        return "store/store";
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
