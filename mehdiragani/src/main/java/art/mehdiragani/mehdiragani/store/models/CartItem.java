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
        this.quantity = quantity;
        this.cart = cart;
    }
    
    // ========== RICH DOMAIN MODDEL BEHAVIOR ========== //
    
    /**
     * Safely increases quantity with validation
     */
    public void increaseQuantity(int amount) {
        validateAmount(amount);
        quantity += amount;
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
        BigDecimal lineTotal = new BigDecimal(artwork.getPrice() * quantity);
        return lineTotal;
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
