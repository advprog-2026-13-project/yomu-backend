package id.ac.ui.cs.advprog.yomu.backend.social.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.application.ClanService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ClanControllerTest {

  private static final UUID CLAN_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();

  @Mock private ClanService clanService;

  @InjectMocks private ClanController clanController;

  private MockMvc mockMvc;
  private SecurityUser securityUser;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(clanController)
            .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
            .setControllerAdvice(new SocialExceptionHandler())
            .build();

    User user = new User("user", "User", "user@mail.com", "0800", "hashed", Role.USER);
    user.setId(USER_ID);
    securityUser = new SecurityUser(user);
    SecurityContextHolder.getContext().setAuthentication(userPrincipal());
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private UsernamePasswordAuthenticationToken userPrincipal() {
    return new UsernamePasswordAuthenticationToken(
        securityUser, null, securityUser.getAuthorities());
  }

  // ---- deleteClan ----

  @Test
  void deleteClan_asLeader_returns204() throws Exception {
    doNothing().when(clanService).deleteClan(any(), any());

    mockMvc
        .perform(delete("/api/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isNoContent());

    verify(clanService).deleteClan(CLAN_ID, USER_ID);
  }

  @Test
  void deleteClan_asNonLeader_returns403() throws Exception {
    doThrow(new NotClanLeaderException("Only the clan leader can delete the clan"))
        .when(clanService)
        .deleteClan(any(), any());

    mockMvc
        .perform(delete("/api/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteClan_clanNotFound_returns404() throws Exception {
    doThrow(new ClanNotFoundException("Clan not found"))
        .when(clanService)
        .deleteClan(any(), any());

    mockMvc
        .perform(delete("/api/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isNotFound());
  }
}
