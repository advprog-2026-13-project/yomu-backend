package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final int BEARER_PREFIX_LENGTH = 7;

  private final JwtService jwtService;
  private final UserRepository userRepository;

  public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    try {
      String token = header.substring(BEARER_PREFIX_LENGTH).trim();
      var payload = jwtService.parse(token);
      String userIdStr = payload.userId();

      if (userIdStr == null || userIdStr.isBlank()) {
        log.warn("JWT payload missing user ID");
        chain.doFilter(request, response);
        return;
      }

      UUID userId = UUID.fromString(userIdStr);
      var userOpt = userRepository.findById(userId);

      if (userOpt.isEmpty()) {
        log.warn("User not found for ID {}", userIdStr);
        chain.doFilter(request, response);
        return;
      }

      var user = userOpt.get();
      MDC.put("username", user.getUsername());

      SecurityUser principal = new SecurityUser(user);
      UsernamePasswordAuthenticationToken auth =
          new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);

      log.debug("User '{}' authenticated", user.getUsername());

    } catch (JwtException | IllegalArgumentException e) {
      log.warn("JWT parse failed: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(request, response);
  }
}
