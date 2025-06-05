package art.mehdiragani.mehdiragani.core.specifications;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import art.mehdiragani.mehdiragani.core.models.Artwork;
import art.mehdiragani.mehdiragani.core.models.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.ArtworkStatus;
import art.mehdiragani.mehdiragani.core.models.ArtworkTheme;

public class ArtworkSpecifications {

    public static Specification<Artwork> priceGreaterOrEqual(BigDecimal minPrice) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Artwork> priceLessOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Artwork> widthGreaterOrEqual(Double minWidth) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("width"), minWidth);
    }

    public static Specification<Artwork> widthLessOrEqual(Double maxWidth) {
        return (root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("width"), maxWidth);
    }

    public static Specification<Artwork> heightGreaterOrEqual(Double minHeight) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("height"), minHeight);
    }

    public static Specification<Artwork> heightLessOrEqual(Double maxHeight) {
        return (root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("height"), maxHeight);
    }

    public static Specification<Artwork> hasTheme(ArtworkTheme theme) {
        return (root, query, cb) -> cb.equal(root.get("theme"), theme);
    }
    
    public static Specification<Artwork> hasFeel(ArtworkFeel feel) {
        return (root, query, cb) -> cb.equal(root.get("feel"), feel);
    }
    
    public static Specification<Artwork> hasStatus(ArtworkStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    
}
