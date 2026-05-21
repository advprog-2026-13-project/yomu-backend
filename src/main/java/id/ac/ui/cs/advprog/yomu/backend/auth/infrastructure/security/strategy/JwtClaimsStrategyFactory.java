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
    this.defaultStrategy = new UserJwtClaimsStrategy();
  }

  public JwtClaimsStrategy select(User user) {
    Role role = user.getRole();
    return strategies.stream()
        .filter(
            s -> {
              if (s instanceof UserJwtClaimsStrategy u) return u.supportedRole() == role;
              if (s instanceof AdminJwtClaimsStrategy a) return a.supportedRole() == role;
              return false;
            })
        .findFirst()
        .orElse(defaultStrategy);
  }
}
