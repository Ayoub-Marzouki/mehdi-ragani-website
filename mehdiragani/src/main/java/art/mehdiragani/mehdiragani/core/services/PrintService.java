package art.mehdiragani.mehdiragani.core.services;

import art.mehdiragani.mehdiragani.core.models.Print;
import art.mehdiragani.mehdiragani.core.repositories.PrintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkFeel;
import art.mehdiragani.mehdiragani.core.models.enums.ArtworkTheme;
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
        if (minPrice != null) {
            spec = spec.and(PrintSpecifications.basePriceGreaterOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(PrintSpecifications.basePriceLessOrEqual(maxPrice));
        }
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
}
