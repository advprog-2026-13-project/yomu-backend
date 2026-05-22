package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.strategy;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JwtClaimsStrategyFactory {

  private final JwtClaimsStrategy defaultStrategy;
  private final List<JwtClaimsStrategy> strategies;

  public JwtClaimsStrategyFactory(List<JwtClaimsStrategy> strategies) {
    this.strategies = strategies;
    this.defaultStrategy =
        strategies.stream()
            .filter(s -> s.supportedRole() == Role.USER)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No USER strategy registered"));
  }

  public JwtClaimsStrategy select(User user) {
    return strategies.stream()
        .filter(s -> s.supportedRole() == user.getRole())
        .findFirst()
        .orElse(defaultStrategy);
  }
}
