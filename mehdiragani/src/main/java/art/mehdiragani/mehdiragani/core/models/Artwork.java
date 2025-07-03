package art.mehdiragani.mehdiragani.core.models;

import java.util.List;
import java.util.UUID;

import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkStatus;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok
@NoArgsConstructor // Lombok
@Entity // JPA
@AllArgsConstructor
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // @Setter(AccessLevel.NONE) // Disables the setter for this specific field (Lombok)
    private UUID id;

    @NotBlank(message = "Title cannot be null, nor entirely blank (at least a whitespace)")
    @Column(nullable = false)
    private String title;

    @Positive(message = "Price must strictly be > 0")
    @Column(nullable = false)
    private double price = 1.00;

    @Positive(message = "Year cannot be negative!")
    @Column
    private int year;

    @Positive(message = "Width cannot be negative!")
    @Column
    private Double width;

    @Positive(message = "Height cannot be negative!")
    @Column
    private Double height;

    @Enumerated(EnumType.STRING)
    @Column
    private ArtworkTheme theme;

    @Enumerated(EnumType.STRING)
    @Column
    private ArtworkFeel feel;
    
    @Enumerated(EnumType.STRING)
    @Column
    private ArtworkStatus status;

    @NotNull(message = "Main image is required")
    @Column(name = "main_image_path")
    private String mainImagePath;

    @ElementCollection
    @CollectionTable(
        name = "secondary_image",
        joinColumns = @JoinColumn(name = "artwork_id")
    )
    @Column(name = "secondary_image_path")
    private List<String> secondaryImagesPaths;
}
