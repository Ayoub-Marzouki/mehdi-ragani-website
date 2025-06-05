package art.mehdiragani.mehdiragani.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.models.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.ArtworkStatus;
import art.mehdiragani.mehdiragani.core.models.ArtworkTheme;
import art.mehdiragani.mehdiragani.core.services.ArtworkService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/artworks")
public class AdminArtworkController {
    private final ArtworkService artworkService;

    @Autowired
    public AdminArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping
    public String listArtworks(Model model) {
        List<Artwork> artworks = artworkService.getAllArtworks();
        model.addAttribute("artworks", artworks);
        return "admin/artworks";
    }

    // “Add Artwork” form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Supply an empty Artwork for data-binding
        model.addAttribute("artwork", new Artwork());
        model.addAttribute("allThemes",  List.of(ArtworkTheme.values()));
        model.addAttribute("allFeels",   List.of(ArtworkFeel.values()));
        model.addAttribute("allStatuses",List.of(ArtworkStatus.values()));
        
        return "admin/add-artwork";
    }
 
    // 2) Handle the form submission
    @PostMapping("/add")
    public String handleAdd(@Valid @ModelAttribute("artwork") Artwork artwork, BindingResult binding) {
        if (binding.hasErrors()) {
            // If validation fails, redisplay the form with error messages
            return "admin/add-artwork";
        }

        // Clean out any blank or null entries so we don’t persist empty strings (which register as 'Secondary Image') as paths
        if (artwork.getSecondaryImagesPaths() != null) {
            artwork.getSecondaryImagesPaths().removeIf(p -> p == null || p.isBlank());
        }

        artworkService.createArtwork(artwork);
        // Redirect back to the listing page on success
        return "redirect:/admin/artworks";
    }

    @PostMapping("/{id}/delete")
    public String deleteArtwork(@PathVariable("id") UUID id) {
        // 1) Load or at least check it exists
        Artwork artwork = artworkService.getArtworkById(id);

        artworkService.deleteArtwork(artwork);
        return "redirect:/admin/artworks";
    }

    // Show the edit form, pre‑loading the Artwork by its fields
    @GetMapping("/{id}/change")
    public String showEditForm(@PathVariable("id") UUID id, Model model) {
        Artwork art = artworkService.getArtworkById(id);
        model.addAttribute("artwork", art);
        model.addAttribute("allThemes",  List.of(ArtworkTheme.values()));
        model.addAttribute("allFeels",   List.of(ArtworkFeel.values()));
        model.addAttribute("allStatuses",List.of(ArtworkStatus.values()));

        return "admin/edit-artwork";
    }

    // Handle the update submission
    @PostMapping("/{id}/change")
    public String updateArtwork(
            @PathVariable("id") UUID id,
            @Valid @ModelAttribute("artwork") Artwork artwork,
            BindingResult binding) {

        // clean out any blanks
        if (artwork.getSecondaryImagesPaths() != null) {
        artwork.getSecondaryImagesPaths().removeIf(p -> p == null || p.isBlank());
        }

        if (binding.hasErrors()) {
            return "admin/edit-artwork";
        }
        artwork.setId(id);
        artworkService.updateArtwork(artwork);
        return "redirect:/admin/artworks";
    }


}
