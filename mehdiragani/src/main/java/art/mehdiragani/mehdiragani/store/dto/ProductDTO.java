package art.mehdiragani.mehdiragani.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * ProductDTO is a unified Data Transfer Object for displaying both artworks and prints
 * in the store listing. It encapsulates the minimal fields needed for grid display and filtering,
 * and allows the template to distinguish between product types ("ARTWORK" or "PRINT")
 * in a type-safe, maintainable way.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String type; // "ARTWORK" or "PRINT"
    private UUID id;
    private String title;
    private String mainImagePath;
    private Double price; // for artworks
    private Double basePrice; // for prints
    private String status; // for artworks (enum name)
    private String statusDisplayName; // for artworks (display name)
    private Integer year; // for artworks
} 