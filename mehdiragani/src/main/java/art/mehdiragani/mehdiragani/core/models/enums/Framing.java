package art.mehdiragani.mehdiragani.core.models.enums;

public enum Framing {
    NONE("None"),
    BLACK("Black"),
    WHITE("White");

    private final String displayName;

    Framing(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 