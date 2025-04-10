package art.mehdiragani.mehdiragani.core.models;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data // Lombok
@NoArgsConstructor // Lombok
@Entity // JPA
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE) // Disables the setter for this specific field (Lombok)
    private UUID id;

    @NotBlank(message = "Title cannot be null, nor entirely blank (at least a whitespace)")
    @Column(nullable = false)
    private String title;

    @Positive(message = "Price must strictly be > 0")
    @Column(nullable = false)
    private double price;

    @NotNull(message = "Main image is required")
    @Column(name = "main_image_path")
    private String mainImagePath;

    @ElementCollection
    @CollectionTable(
        name = "secondary_images",
        joinColumns = @JoinColumn(name = "artwork_id")
    )
    @Column(name = "secondary_image_path")
    private List<String> secondaryImagesPaths;


    public Artwork(String title, double price, String mainImagePath, List<String> secondaryImagesPaths) {
        this.title = title;
        this.price = price;
        this.mainImagePath = mainImagePath;
        this.secondaryImagesPaths = secondaryImagesPaths;
    }
}
