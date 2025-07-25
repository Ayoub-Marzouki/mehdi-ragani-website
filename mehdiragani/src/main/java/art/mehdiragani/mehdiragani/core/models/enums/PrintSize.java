package art.mehdiragani.mehdiragani.core.models.enums;

public enum PrintSize {
    SMALL_20x20(20, 20, "20x20 cm"),
    MEDIUM_30x40(30, 40, "30x40 cm"),
    MEDIUM_40x40(40, 40, "40x40 cm"),
    LARGE_50x70(50, 70, "50x70 cm"),
    LARGE_60x80(60, 80, "60x80 cm");

    private final int width;
    private final int height;
    private final String displayName;

    PrintSize(int width, int height, String displayName) {
        this.width = width;
        this.height = height;
        this.displayName = displayName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getAspectRatio() {
        return (double) width / height;
    }

    public String getDisplayName() {
        return displayName;
    }
} 