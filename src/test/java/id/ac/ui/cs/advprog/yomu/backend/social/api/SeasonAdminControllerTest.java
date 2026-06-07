package id.ac.ui.cs.advprog.yomu.backend.social.api;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.application.SeasonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SeasonAdminControllerTest {

  @Mock private SeasonService seasonService;

  @InjectMocks private SeasonAdminController seasonAdminController;

  private MockMvc mockMvc;
  private SecurityUser adminSecurityUser;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(seasonAdminController)
            .setControllerAdvice(new SocialExceptionHandler())
            .build();

    User admin = new User("admin", "Admin", "admin@mail.com", "0800", "hashed", Role.ADMIN);
    adminSecurityUser = new SecurityUser(admin);
    SecurityContextHolder.getContext().setAuthentication(adminPrincipal());
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private UsernamePasswordAuthenticationToken adminPrincipal() {
    return new UsernamePasswordAuthenticationToken(
        adminSecurityUser, null, adminSecurityUser.getAuthorities());
  }

  @Test
  void endSeason_asAdmin_returns200AndInvokesService() throws Exception {
    doNothing().when(seasonService).endSeason();

    mockMvc
        .perform(post("/api/admin/social/seasons/end").principal(adminPrincipal()))
        .andExpect(status().isOk());

    verify(seasonService).endSeason();
  }
}
