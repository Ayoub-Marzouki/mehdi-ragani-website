package art.mehdiragani.mehdiragani.payment.enums;

public enum OrderStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    SHIPPED("Shipped"),
    COMPLETED("Completed"),
    CANCELED("Canceled");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getDisplayName() {
        return status;
    }
}
