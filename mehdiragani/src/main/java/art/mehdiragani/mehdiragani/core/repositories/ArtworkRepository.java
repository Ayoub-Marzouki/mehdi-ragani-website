package art.mehdiragani.mehdiragani.core.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import art.mehdiragani.mehdiragani.core.models.Artwork;

public interface ArtworkRepository extends JpaRepository<Artwork, UUID> {
    
}
