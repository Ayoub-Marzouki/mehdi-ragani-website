package art.mehdiragani.mehdiragani.payment.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PayPalCaptureResponse {
    private String id;
    private String status;

    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;

    @Data
    public static class PurchaseUnit {
        private Payments payments;
    }

    @Data
    public static class Payments {
        private List<Capture> captures;
    }

    @Data
    public static class Capture {
        private String id;
        private String status;
        // You can add more fields like 'amount', 'create_time', etc.
    }

    /**
     * A helper method to safely extract the final capture ID.
     * @return The ID of the first completed capture, or null if not found.
     */
    public String findCompletedCaptureId() {
        if (purchaseUnits == null || purchaseUnits.isEmpty()) return null;
        
        Capture capture = purchaseUnits.get(0).getPayments().getCaptures().stream()
            .filter(c -> "COMPLETED".equalsIgnoreCase(c.getStatus()))
            .findFirst()
            .orElse(null);

        return capture != null ? capture.getId() : null;
    }
}
