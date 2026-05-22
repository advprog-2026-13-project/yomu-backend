package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.strategy;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import java.util.Map;

public interface JwtClaimsStrategy {

  Map<String, Object> buildClaims(User user);

  Role supportedRole();
}
