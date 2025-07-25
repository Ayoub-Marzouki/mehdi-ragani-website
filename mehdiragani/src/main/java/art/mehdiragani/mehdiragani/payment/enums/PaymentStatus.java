package art.mehdiragani.mehdiragani.payment.enums;

public enum PaymentStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed");

    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    public String displayName() {
        return displayName;
    }
}
