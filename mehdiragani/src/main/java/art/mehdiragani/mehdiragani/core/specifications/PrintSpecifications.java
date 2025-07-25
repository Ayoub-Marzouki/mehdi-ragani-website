package art.mehdiragani.mehdiragani.core.specifications;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;

public class PrintSpecifications {
    public static Specification<Print> basePriceGreaterOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
    }

    public static Specification<Print> basePriceLessOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
    }

    public static Specification<Print> widthGreaterOrEqual(Double minWidth) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("width"), minWidth);
    }

    public static Specification<Print> widthLessOrEqual(Double maxWidth) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("width"), maxWidth);
    }

    public static Specification<Print> heightGreaterOrEqual(Double minHeight) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("height"), minHeight);
    }

    public static Specification<Print> heightLessOrEqual(Double maxHeight) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("height"), maxHeight);
    }

    public static Specification<Print> hasTheme(ArtworkTheme theme) {
        return (root, query, cb) -> cb.equal(root.get("theme"), theme);
    }

    public static Specification<Print> hasFeel(ArtworkFeel feel) {
        return (root, query, cb) -> cb.equal(root.get("feel"), feel);
    }
} 