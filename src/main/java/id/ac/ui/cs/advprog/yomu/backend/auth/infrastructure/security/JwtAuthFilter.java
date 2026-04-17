package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
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
      String token = header.substring(7).trim();
      var payload = jwtService.parse(token);
      String userIdStr = payload.userId();

      if (userIdStr != null && !userIdStr.isBlank()) {
        UUID userId = UUID.fromString(userIdStr);

        userRepository
            .findById(userId)
            .ifPresent(
                user -> {
                  SecurityUser principal = new SecurityUser(user);
                  UsernamePasswordAuthenticationToken auth =
                      new UsernamePasswordAuthenticationToken(
                          principal, null, principal.getAuthorities());
                  SecurityContextHolder.getContext().setAuthentication(auth);
                });
      } else {
        log.warn("JWT payload missing user ID");
      }

    } catch (Exception e) {
      log.error("JWT Authentication failed: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(request, response);
  }
}
