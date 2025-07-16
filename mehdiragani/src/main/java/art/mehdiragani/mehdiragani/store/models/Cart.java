package art.mehdiragani.mehdiragani.store.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import art.mehdiragani.mehdiragani.auth.models.User;
import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Optional association (guest carts won't have a user)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Unique session ID for guest carts
    @Column(name = "session_id", unique = true)
    private String sessionId;
    
    // Bidirectional relationship with orphan removal
    @OneToMany(
        mappedBy = "cart",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<CartItem> items = new ArrayList<CartItem>(); 
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    
    
    // ========== RICH DOMAIN MODEL BEHAVIOR ========== //
    
    /**
     * Adds artwork to cart or updates quantity if already present
     * Enforces business rules:
     *  - Artwork must not be sold
     *  - Quantity must be positive
     *  - Automatic timestamp update
     */
    public void addItem(Artwork artwork, int quantity) {
        validateArtwork(artwork);
        validateQuantity(quantity);
        
        // If item exists increase its quantity, otherwise add it
        findItemByArtwork(artwork.getId())
            .ifPresentOrElse(
                item -> item.increaseQuantity(quantity),
                () -> items.add(new CartItem(artwork, quantity, this))
            );
        
        markUpdated();
    }
    
    /**
     * Removes item by artwork ID
     * Automatic timestamp update on change
     */
    public void removeItem(UUID artworkId) {
        boolean removed = items.removeIf(
            item -> item.getArtwork().getId().equals(artworkId)
        );
        
        if (removed) markUpdated();
    }
    
    /**
     * Updates quantity for specific artwork
     * Throws exception if item not found
     */
    public void updateQuantity(UUID artworkId, int newQuantity) {
        validateQuantity(newQuantity);
        CartItem item = findItemByArtwork(artworkId)
            .orElseThrow(() -> new IllegalStateException("Artwork not in cart"));
        item.setQuantity(newQuantity);
        markUpdated();
    }
    
    /**
     * Calculates total cart value (transient property)
     */
    @Transient
    public BigDecimal getTotalPrice() {
        return items.stream()
            .map(CartItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // ========== INTERNAL HELPERS ========== //
    
    private void markUpdated() {
        this.updatedAt = OffsetDateTime.now();
    }
    
    private Optional<CartItem> findItemByArtwork(UUID artworkId) {
        return items.stream()
            .filter(item -> item.getArtwork().getId().equals(artworkId))
            .findFirst();
    }
    
    private void validateArtwork(Artwork artwork) {
        if (artwork.getStatus() == ArtworkStatus.Sold) {
            throw new IllegalStateException("Cannot add sold artwork to cart");
        }
    }
    
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
}
