package art.mehdiragani.mehdiragani.core.models.enums;

public enum PrintType {
    CANVAS("Canvas"),
    FINE_ART_PAPER("Fine Art Paper"),
    PHOTO_PAPER("Photo Paper"),
    VINYL("Vinyl");

    private final String displayName;

    PrintType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 