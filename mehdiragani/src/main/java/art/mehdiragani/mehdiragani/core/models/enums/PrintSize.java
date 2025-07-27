package art.mehdiragani.mehdiragani.core.models.enums;

public enum PrintSize {
    SIZE_10x10(10, 10, "10x10 cm"),
    SIZE_15x15(15, 15, "15x15 cm"),
    SIZE_15x20(15, 20, "15x20 cm"),
    SIZE_20x20(20, 20, "20x20 cm"),
    SIZE_18x24(18, 24, "18x24 cm"),
    SIZE_20x25(20, 25, "20x25 cm"),
    SIZE_25x25(25, 25, "25x25 cm"),
    SIZE_20x30(20, 30, "20x30 cm"),
    SIZE_25x30(25, 30, "25x30 cm"),
    SIZE_30x30(30, 30, "30x30 cm"),
    SIZE_20x40(20, 40, "20x40 cm"),
    SIZE_30x35(30, 35, "30x35 cm"),
    SIZE_30x40(30, 40, "30x40 cm"),
    SIZE_30x50(30, 50, "30x50 cm"),
    SIZE_40x40(40, 40, "40x40 cm"),
    SIZE_30x60(30, 60, "30x60 cm"),
    SIZE_40x50(40, 50, "40x50 cm"),
    SIZE_50x50(50, 50, "50x50 cm"),
    SIZE_40x60(40, 60, "40x60 cm"),
    SIZE_50x60(50, 60, "50x60 cm"),
    SIZE_40x70(40, 70, "40x70 cm"),
    SIZE_40x80(40, 80, "40x80 cm"),
    SIZE_50x70(50, 70, "50x70 cm"),
    SIZE_60x60(60, 60, "60x60 cm"),
    SIZE_60x70(60, 70, "60x70 cm"),
    SIZE_70x70(70, 70, "70x70 cm"),
    SIZE_60x80(60, 80, "60x80 cm"),
    SIZE_60x90(60, 90, "60x90 cm"),
    SIZE_80x80(80, 80, "80x80 cm"),
    SIZE_70x90(70, 90, "70x90 cm"),
    SIZE_70x100(70, 100, "70x100 cm"),
    SIZE_80x100(80, 100, "80x100 cm"),
    SIZE_100x100(100, 100, "100x100 cm"),
    SIZE_100x120(100, 120, "100x120 cm");

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