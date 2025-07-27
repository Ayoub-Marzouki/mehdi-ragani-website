package art.mehdiragani.mehdiragani.core.models;

import art.mehdiragani.mehdiragani.core.models.enums.PrintType;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Print {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String mainImagePath;

    @Positive
    @Column(nullable = false)
    private Double width;

    @Positive
    @Column(nullable = false)
    private Double height;

    @Positive
    @Column(nullable = false)
    private double basePrice;

    @Column(nullable = true)
    private Integer stock; // null means unlimited

    @Column(nullable = true)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ArtworkFeel feel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ArtworkTheme theme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrintType printType;

    @Enumerated(EnumType.STRING)
    @Column(name = "print_size", nullable = false, length = 15)
    private PrintSize printSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Framing framing;

    public Double getAspectRatio() {
        if (width == null || height == null || height == 0) return null;
        return width / height;
    }

} 