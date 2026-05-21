package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.strategy;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AdminJwtClaimsStrategy implements JwtClaimsStrategy {

  @Override
  public Map<String, Object> buildClaims(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", user.getUsername());
    claims.put("role", user.getRole().name());
    claims.put("scope", "admin-full");
    return claims;
  }

  @Override
  public Role supportedRole() {
    return Role.ADMIN;
  }
}
