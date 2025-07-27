package art.mehdiragani.mehdiragani.core.services;

import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.repositories.PrintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
import art.mehdiragani.mehdiragani.core.models.enums.PrintSize;
import art.mehdiragani.mehdiragani.core.models.enums.PrintType;
import art.mehdiragani.mehdiragani.core.models.enums.Framing;
import org.springframework.data.jpa.domain.Specification;
import art.mehdiragani.mehdiragani.core.specifications.PrintSpecifications;

@Service
@Transactional
public class PrintService {
    private final PrintRepository printRepository;

    public PrintService(PrintRepository printRepository) {
        this.printRepository = printRepository;
    }

    public List<Print> getAllPrints() {
        return printRepository.findAll();
    }

    public Print getPrintById(UUID id) {
        return printRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Print not found"));
    }

    public Print savePrint(Print print) {
        return printRepository.save(print);
    }

    public void deletePrintById(UUID id) {
        printRepository.deleteById(id);
    }

    public void deletePrint(Print print) {
        printRepository.delete(print);
    }

    public List<Print> findByCriteria(
        BigDecimal minPrice, BigDecimal maxPrice,
        Double minWidth, Double maxWidth,
        Double minHeight, Double maxHeight,
        ArtworkTheme theme, ArtworkFeel feel
    ) {
        Specification<Print> spec = Specification.where(null);
        // Note: Price filtering removed since pricing is now based on print size
        // Price filtering would need to be implemented differently if needed
        if (minWidth != null) {
            spec = spec.and(PrintSpecifications.widthGreaterOrEqual(minWidth));
        }
        if (maxWidth != null) {
            spec = spec.and(PrintSpecifications.widthLessOrEqual(maxWidth));
        }
        if (minHeight != null) {
            spec = spec.and(PrintSpecifications.heightGreaterOrEqual(minHeight));
        }
        if (maxHeight != null) {
            spec = spec.and(PrintSpecifications.heightLessOrEqual(maxHeight));
        }
        if (theme != null) {
            spec = spec.and(PrintSpecifications.hasTheme(theme));
        }
        if (feel != null) {
            spec = spec.and(PrintSpecifications.hasFeel(feel));
        }
        return printRepository.findAll(spec);
    }

    /**
     * Get compatible print sizes based on artwork aspect ratio
     */
    public List<PrintSize> getCompatiblePrintSizes(Double aspectRatio) {
        if (aspectRatio == null) return List.of();
        
        double tolerance = 0.01; // Allow small rounding differences
        
        return Arrays.stream(PrintSize.values())
                .filter(size -> Math.abs(size.getAspectRatio() - aspectRatio) < tolerance)
                .sorted((a, b) -> Double.compare(getPrintSizePrice(a), getPrintSizePrice(b))) // Sort by price ascending
                .collect(Collectors.toList());
    }

    /**
     * Calculate total price for a print with given options
     */
    public double calculatePrintPrice(Print print, PrintSize size, PrintType type, Framing framing, int quantity) {
        // Use the print's basePrice as foundation, then add size multiplier and other costs
        double basePrice = print.getBasePrice();
        double sizeMultiplier = getPrintSizeMultiplier(size);
        double typeCost = getPrintTypeCost(type);
        double framingCost = getFramingCost(framing);
        return (basePrice * sizeMultiplier + typeCost + framingCost) * quantity;
    }

    /**
     * Calculate total price for a print with given options (overloaded for when we don't have the print object)
     */
    public double calculatePrintPrice(PrintSize size, PrintType type, Framing framing, int quantity) {
        // Fallback to the old system if no print object is provided
        double basePrice = getPrintSizePrice(size);
        double typeCost = getPrintTypeCost(type);
        double framingCost = getFramingCost(framing);
        return (basePrice + typeCost + framingCost) * quantity;
    }

    /**
     * Get size multiplier based on print size (relative to base price)
     * Baseline is now 50x50 (1.0 multiplier)
     */
    private double getPrintSizeMultiplier(PrintSize size) {
        switch (size) {
            case SIZE_10x10: return 0.18; // 18% of base price
            case SIZE_15x15: return 0.24; // 24% of base price
            case SIZE_15x20: return 0.27; // 27% of base price
            case SIZE_20x20: return 0.36; // 36% of base price
            case SIZE_18x24: return 0.36; // 36% of base price
            case SIZE_20x25: return 0.40; // 40% of base price
            case SIZE_25x25: return 0.44; // 44% of base price
            case SIZE_20x30: return 0.44; // 44% of base price
            case SIZE_25x30: return 0.47; // 47% of base price
            case SIZE_30x30: return 0.51; // 51% of base price
            case SIZE_20x40: return 0.51; // 51% of base price
            case SIZE_30x35: return 0.55; // 55% of base price
            case SIZE_30x40: return 0.58; // 58% of base price
            case SIZE_30x50: return 0.65; // 65% of base price
            case SIZE_40x40: return 0.65; // 65% of base price
            case SIZE_30x60: return 0.78; // 78% of base price
            case SIZE_40x50: return 0.78; // 78% of base price
            case SIZE_50x50: return 1.0;  // 100% of base price (baseline)
            case SIZE_40x60: return 1.0;  // 100% of base price
            case SIZE_50x60: return 1.07; // 107% of base price
            case SIZE_40x70: return 1.07; // 107% of base price
            case SIZE_40x80: return 1.18; // 118% of base price
            case SIZE_50x70: return 1.18; // 118% of base price
            case SIZE_60x60: return 1.18; // 118% of base price
            case SIZE_60x70: return 1.31; // 131% of base price
            case SIZE_70x70: return 1.55; // 155% of base price
            case SIZE_60x80: return 1.55; // 155% of base price
            case SIZE_60x90: return 1.78; // 178% of base price
            case SIZE_80x80: return 2.15; // 215% of base price
            case SIZE_70x90: return 2.15; // 215% of base price
            case SIZE_70x100: return 2.49; // 249% of base price
            case SIZE_80x100: return 2.73; // 273% of base price
            case SIZE_100x100: return 3.0;  // 300% of base price
            case SIZE_100x120: return 3.62; // 362% of base price
            default: return 1.0;
        }
    }

    /**
     * Get price for a specific print size (for backward compatibility)
     * Updated to EUR pricing (converted from MAD)
     */
    private double getPrintSizePrice(PrintSize size) {
        switch (size) {
            case SIZE_10x10: return 0.91; // 10.00 MAD / 11 ≈ 0.91 EUR
            case SIZE_15x15: return 1.18; // 13.00 MAD / 11 ≈ 1.18 EUR
            case SIZE_15x20: return 1.36; // 15.00 MAD / 11 ≈ 1.36 EUR
            case SIZE_20x20: return 1.82; // 20.00 MAD / 11 ≈ 1.82 EUR
            case SIZE_18x24: return 1.82; // 20.00 MAD / 11 ≈ 1.82 EUR
            case SIZE_20x25: return 2.00; // 22.00 MAD / 11 ≈ 2.00 EUR
            case SIZE_25x25: return 2.18; // 24.00 MAD / 11 ≈ 2.18 EUR
            case SIZE_20x30: return 2.18; // 24.00 MAD / 11 ≈ 2.18 EUR
            case SIZE_25x30: return 2.36; // 26.00 MAD / 11 ≈ 2.36 EUR
            case SIZE_30x30: return 2.55; // 28.00 MAD / 11 ≈ 2.55 EUR
            case SIZE_20x40: return 2.55; // 28.00 MAD / 11 ≈ 2.55 EUR
            case SIZE_30x35: return 2.73; // 30.00 MAD / 11 ≈ 2.73 EUR
            case SIZE_30x40: return 2.91; // 32.00 MAD / 11 ≈ 2.91 EUR
            case SIZE_30x50: return 3.27; // 36.00 MAD / 11 ≈ 3.27 EUR
            case SIZE_40x40: return 3.27; // 36.00 MAD / 11 ≈ 3.27 EUR
            case SIZE_30x60: return 3.91; // 43.00 MAD / 11 ≈ 3.91 EUR
            case SIZE_40x50: return 3.91; // 43.00 MAD / 11 ≈ 3.91 EUR
            case SIZE_50x50: return 5.00; // 55.00 MAD / 11 ≈ 5.00 EUR
            case SIZE_40x60: return 5.00; // 55.00 MAD / 11 ≈ 5.00 EUR
            case SIZE_50x60: return 5.36; // 59.00 MAD / 11 ≈ 5.36 EUR
            case SIZE_40x70: return 5.36; // 59.00 MAD / 11 ≈ 5.36 EUR
            case SIZE_40x80: return 5.91; // 65.00 MAD / 11 ≈ 5.91 EUR
            case SIZE_50x70: return 5.91; // 65.00 MAD / 11 ≈ 5.91 EUR
            case SIZE_60x60: return 5.91; // 65.00 MAD / 11 ≈ 5.91 EUR
            case SIZE_60x70: return 6.55; // 72.00 MAD / 11 ≈ 6.55 EUR
            case SIZE_70x70: return 7.73; // 85.00 MAD / 11 ≈ 7.73 EUR
            case SIZE_60x80: return 7.73; // 85.00 MAD / 11 ≈ 7.73 EUR
            case SIZE_60x90: return 8.91; // 98.00 MAD / 11 ≈ 8.91 EUR
            case SIZE_80x80: return 10.73; // 118.00 MAD / 11 ≈ 10.73 EUR
            case SIZE_70x90: return 10.73; // 118.00 MAD / 11 ≈ 10.73 EUR
            case SIZE_70x100: return 12.45; // 137.00 MAD / 11 ≈ 12.45 EUR
            case SIZE_80x100: return 13.64; // 150.00 MAD / 11 ≈ 13.64 EUR
            case SIZE_100x100: return 15.00; // 165.00 MAD / 11 ≈ 15.00 EUR
            case SIZE_100x120: return 18.09; // 199.00 MAD / 11 ≈ 18.09 EUR
            default: return 0.0;
        }
    }

    /**
     * Get cost for print type (updated to EUR pricing)
     */
    private double getPrintTypeCost(PrintType type) {
        switch (type) {
            case CANVAS: return 9.09; // 100.0 MAD / 11 ≈ 9.09 EUR
            case FINE_ART_PAPER: return 4.55; // 50.0 MAD / 11 ≈ 4.55 EUR
            case PHOTO_PAPER: return 2.73; // 30.0 MAD / 11 ≈ 2.73 EUR
            case VINYL: return 1.82; // 20.0 MAD / 11 ≈ 1.82 EUR
            default: return 0.0;
        }
    }

    /**
     * Get cost for framing (updated to EUR pricing)
     */
    private double getFramingCost(Framing framing) {
        switch (framing) {
            case BLACK: return 7.27; // 80.0 MAD / 11 ≈ 7.27 EUR
            case WHITE: return 7.27; // 80.0 MAD / 11 ≈ 7.27 EUR
            case NONE: return 0.0;
            default: return 0.0;
        }
    }
}
