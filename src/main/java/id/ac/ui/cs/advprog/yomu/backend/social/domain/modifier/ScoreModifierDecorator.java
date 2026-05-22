package id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier;

public abstract class ScoreModifierDecorator implements ScoreModifier {

  protected final ScoreModifier inner;

  protected ScoreModifierDecorator(ScoreModifier inner) {
    this.inner = inner;
  }
}
