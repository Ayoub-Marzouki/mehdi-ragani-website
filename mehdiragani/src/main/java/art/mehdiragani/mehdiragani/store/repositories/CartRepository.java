package art.mehdiragani.mehdiragani.store.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import art.mehdiragani.mehdiragani.store.models.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    // For authenticated users
    Optional<Cart> findByUserId(UUID userId);

    // For guest users
    Optional<Cart> findBySessionId(String sessionId);
    
}
