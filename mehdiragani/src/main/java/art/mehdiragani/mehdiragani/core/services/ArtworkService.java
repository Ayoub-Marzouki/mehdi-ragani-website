package art.mehdiragani.mehdiragani.core.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import art.mehdiragani.mehdiragani.core.repositories.ArtworkRepository;
import art.mehdiragani.mehdiragani.core.specifications.ArtworkSpecifications;
import jakarta.validation.Valid;

@Service
@Validated
public class ArtworkService {
    private final ArtworkRepository artworkRepository; // final here to guarantee that the injected dependency won’t change unexpectedly, improving thread safety and code clarity. It's a spring boot best practice.

    public ArtworkService(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }

    public Artwork createArtwork(@Valid Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    public Artwork updateArtwork(@Valid Artwork artwork) {
        return artworkRepository.save(artwork);
    }

    public void deleteArtwork(Artwork artwork) {
        artworkRepository.delete(artwork);
    }

    public Artwork getArtworkById(UUID id) {
        return artworkRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Artwork not found"));
    }

    @Transactional(readOnly = true)
    public List<Artwork> findByCriteria(
        BigDecimal minPrice, BigDecimal maxPrice,
        Double minWidth,    Double maxWidth,
        Double minHeight,   Double maxHeight,
        ArtworkTheme theme, ArtworkFeel feel, ArtworkStatus status
    ) {
        // Start with a “no‐condition” specification (equivalent of selectAll)
        Specification<Artwork> spec = Specification.where(null);

        if (minPrice != null) {
            spec = spec.and(ArtworkSpecifications.priceGreaterOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ArtworkSpecifications.priceLessOrEqual(maxPrice));
        }
        if (minWidth != null) {
            spec = spec.and(ArtworkSpecifications.widthGreaterOrEqual(minWidth));
        }
        if (maxWidth != null) {
            spec = spec.and(ArtworkSpecifications.widthLessOrEqual(maxWidth));
        }
        if (minHeight != null) {
            spec = spec.and(ArtworkSpecifications.heightGreaterOrEqual(minHeight));
        }
        if (maxHeight != null) {
            spec = spec.and(ArtworkSpecifications.heightLessOrEqual(maxHeight));
        }
        if (theme != null) {
            spec = spec.and(ArtworkSpecifications.hasTheme(theme));
        }
        if (feel != null) {
            spec = spec.and(ArtworkSpecifications.hasFeel(feel));
        }
        if (status != null) {
            spec = spec.and(ArtworkSpecifications.hasStatus(status));
        }
        

        // If spec is still “null” (no filters), findAll(null) returns all rows.
        return artworkRepository.findAll(spec);
    }
}

