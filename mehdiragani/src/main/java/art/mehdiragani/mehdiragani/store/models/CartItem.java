package art.mehdiragani.mehdiragani.store.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import art.mehdiragani.mehdiragani.core.models.Artwork;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artwork_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Artwork artwork;
    
    @Column(nullable = false)
    private int quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @Column(name = "added_at", nullable = false, updatable = false)
    private OffsetDateTime addedAt = OffsetDateTime.now();


    public CartItem(Artwork artwork, int quantity, Cart cart) {
        this.artwork = artwork;
        // Artworks are unique items, so quantity should always be 1
        this.quantity = 1;
        this.cart = cart;
    }
    
    // ========== RICH DOMAIN MODEL BEHAVIOR ========== //
    
    /**
     * Override setter to ensure quantity is always 1 for unique artworks
     */
    public void setQuantity(int quantity) {
        // Artworks are unique items, so quantity should always be 1
        this.quantity = 1;
    }
    
    /**
     * Safely increases quantity with validation
     * Note: For unique artworks, this method is not applicable
     */
    public void increaseQuantity(int amount) {
        throw new UnsupportedOperationException("Cannot increase quantity for unique artwork items");
    }
    
    /**
     * Safely decreases quantity with validation
     * Prevents negative quantities
     */
    public void decreaseQuantity(int amount) {
        validateAmount(amount);
        if (amount > quantity) {
            throw new IllegalArgumentException(
                "Cannot remove more items than present in cart"
            );
        }
        quantity -= amount;
    }
    
    /**
     * Calculates item total price (price × quantity)
     */
    @Transient
    public BigDecimal getLineTotal() {
        BigDecimal price = BigDecimal.valueOf(artwork.getPrice());
        BigDecimal qty = BigDecimal.valueOf(quantity);
        return price.multiply(qty);
    }
    
    // ========== INTERNAL VALIDATION ========== //
    
    private void validateAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Quantity change must be positive"
            );
        }
    }
}
