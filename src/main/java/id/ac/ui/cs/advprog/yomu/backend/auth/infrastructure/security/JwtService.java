package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.strategy.JwtClaimsStrategyFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
  private final byte[] secret;
  private final long expirationMinutes;
  private final JwtClaimsStrategyFactory strategyFactory;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-minutes:120}") long expirationMinutes,
      JwtClaimsStrategyFactory strategyFactory) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    this.expirationMinutes = expirationMinutes;
    this.strategyFactory = strategyFactory;
  }

  public String generateToken(User user) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(expirationMinutes * 60);

    var builder =
        Jwts.builder()
            .subject(user.getId().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(Keys.hmacShaKeyFor(secret));

    strategyFactory.select(user).buildClaims(user).forEach(builder::claim);

    return builder.compact();
  }

  public Payload parse(String token) {
    var claims =
        Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret))
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return new Payload(
        claims.getSubject(),
        claims.get("username", String.class),
        claims.get("role", String.class));
  }

  public record Payload(String userId, String username, String role) {}
}
