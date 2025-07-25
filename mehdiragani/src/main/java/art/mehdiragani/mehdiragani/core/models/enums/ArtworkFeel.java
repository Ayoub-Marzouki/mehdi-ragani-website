package art.mehdiragani.mehdiragani.core.models.enums;

public enum ArtworkFeel {
    CALM("Calm"),
    JOYFUL("Joyful"),
    MELANCHOLIC("Melancholic"),
    NOSTALGIC("Nostalgic");

    private final String displayName;

    ArtworkFeel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
