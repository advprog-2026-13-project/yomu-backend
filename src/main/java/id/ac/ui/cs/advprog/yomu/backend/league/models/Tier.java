package id.ac.ui.cs.advprog.yomu.backend.league.models;

public enum Tier {
    BRONZE(1),
    SILVER(2),
    GOLD(3),
    DIAMOND(4);

    private final int level;

    Tier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(Tier other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(Tier other) {
        return this.level < other.level;
    }
}