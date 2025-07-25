package art.mehdiragani.mehdiragani.store.models;

import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.models.enums.PrintType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PrintCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "print_id", nullable = false)
    private Print print;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrintType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrintSize size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Framing framing;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private double lineTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "added_at", nullable = false, updatable = false)
    private OffsetDateTime addedAt = OffsetDateTime.now();

    public void calculateLineTotal() {
        this.lineTotal = this.unitPrice * this.quantity;
    }
} 