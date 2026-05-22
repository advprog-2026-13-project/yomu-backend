package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public class IdentityModifier implements ScoreModifier {

  @Override
  public long apply(long baseScore) {
    return baseScore;
  }
}
