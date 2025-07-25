package art.mehdiragani.mehdiragani.core.models.enums;

public enum ArtworkStatus {
    AVAILABLE("Available"),
    SOLD("Sold");

    private final String displayName;

    ArtworkStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
