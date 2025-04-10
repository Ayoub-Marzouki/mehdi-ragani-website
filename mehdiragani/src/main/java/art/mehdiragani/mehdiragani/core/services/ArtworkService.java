package art.mehdiragani.mehdiragani.core.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.repositories.ArtworkRepository;
import jakarta.validation.Valid;

@Service
@Validated
public class ArtworkService {
    private final ArtworkRepository artworkRepository; // final here to guarantee that the injected dependency won’t change unexpectedly, improving thread safety and code clarity. It's a spring boot best practice.

    @Autowired
    public ArtworkService(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }

    // Create a new artwork (no validation for now)
    public Artwork createArtwork(@Valid Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    // Update an existing artwork (also no validation)
    public Artwork updateArtwork(@Valid Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    public void deleteArtwork(Artwork artwork) {
        artworkRepository.delete(artwork);
    }

    // Retrieve a single artwork by its unique ID.
    public Artwork getArtworkById(UUID id) {
        return artworkRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Artwork not found"));
    }
}

