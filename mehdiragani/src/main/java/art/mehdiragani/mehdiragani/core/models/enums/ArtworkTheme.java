package art.mehdiragani.mehdiragani.core.models.enums;

public enum ArtworkTheme {
    NATURE("Nature"),
    URBAN("Urban"),
    PORTRAIT("Portrait"),
    ABSTRACT("Abstract"),
    SPIRITUAL("Spiritual"),
    CULTURE("Culture"),
    DREAMS("Dreams"),
    CYBERPUNK("Cyberpunk");

    private final String displayName;

    ArtworkTheme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
