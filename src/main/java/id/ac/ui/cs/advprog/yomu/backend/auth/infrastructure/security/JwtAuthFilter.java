package id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
      UUID userId = UUID.fromString(payload.userId());

      var userOpt = userRepository.findById(userId);
      if (userOpt.isPresent()) {
        var principal = new SecurityUser(userOpt.get());
        var auth =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    } catch (Exception ignored) {
      SecurityContextHolder.clearContext();
    }

    chain.doFilter(request, response);
  }
}
