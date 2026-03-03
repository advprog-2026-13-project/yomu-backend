package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {
    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes:120}") long expirationMinutes
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(Keys.hmacShaKeyFor(secret))
                .compact();
    }

    public Payload parse(String token) {
        var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new Payload(
                claims.getSubject(),
                claims.get("username", String.class),
                claims.get("role", String.class)
        );
    }

    public record Payload(String userId, String username, String role) {}
}