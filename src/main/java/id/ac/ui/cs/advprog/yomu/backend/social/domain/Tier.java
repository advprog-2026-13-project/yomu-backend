package id.ac.ui.cs.advprog.yomu.backend.social.domain;

public enum Tier {
  BRONZE,
  SILVER,
  GOLD,
  DIAMOND;

  public Tier nextTier() {
    Tier[] all = values();
    int next = ordinal() + 1;
    return next < all.length ? all[next] : this;
  }

  public Tier previousTier() {
    int prev = ordinal() - 1;
    return prev >= 0 ? values()[prev] : this;
  }
}
