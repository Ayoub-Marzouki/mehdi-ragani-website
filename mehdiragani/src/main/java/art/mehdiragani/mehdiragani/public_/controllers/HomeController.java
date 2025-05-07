package art.mehdiragani.mehdiragani.public_.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.services.ArtworkService;

@Controller
@RequestMapping("/")
public class HomeController {
    private final ArtworkService artworkService;

    @Autowired
    public HomeController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }
    
    @GetMapping
    public String index(Model model) {
        List<Artwork> artworks = artworkService.getAllArtworks();
        model.addAttribute("artworks", artworks);
        
        return "index";
    }
}
