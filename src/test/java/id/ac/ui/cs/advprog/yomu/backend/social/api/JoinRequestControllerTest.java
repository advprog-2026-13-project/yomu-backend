package id.ac.ui.cs.advprog.yomu.backend.social.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.application.JoinRequestService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.DuplicateJoinRequestException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.JoinRequestAlreadyResolvedException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import java.util.List;
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
class JoinRequestControllerTest {

  private static final UUID CLAN_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID REQUEST_ID = UUID.randomUUID();

  @Mock private JoinRequestService joinRequestService;

  @InjectMocks private JoinRequestController joinRequestController;

  private MockMvc mockMvc;
  private SecurityUser securityUser;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(joinRequestController)
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

  // ---- submit ----

  @Test
  void submit_happyPath_returns201WithPendingStatus() throws Exception {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);
    when(joinRequestService.submitJoinRequest(any(), any())).thenReturn(req);

    mockMvc
        .perform(
            post("/api/social/clans/{clanId}/join-requests", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"))
        .andExpect(jsonPath("$.clanId").value(CLAN_ID.toString()));
  }

  @Test
  void submit_duplicatePending_returns409() throws Exception {
    doThrow(new DuplicateJoinRequestException("Already pending"))
        .when(joinRequestService)
        .submitJoinRequest(any(), any());

    mockMvc
        .perform(
            post("/api/social/clans/{clanId}/join-requests", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isConflict());
  }

  // ---- listPending ----

  @Test
  void listPending_asLeader_returns200WithList() throws Exception {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);
    when(joinRequestService.listPendingRequests(any(), any())).thenReturn(List.of(req));

    mockMvc
        .perform(
            get("/api/social/clans/{clanId}/join-requests", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].status").value("PENDING"));
  }

  @Test
  void listPending_asNonLeader_returns403() throws Exception {
    doThrow(new NotClanLeaderException("Not the leader"))
        .when(joinRequestService)
        .listPendingRequests(any(), any());

    mockMvc
        .perform(
            get("/api/social/clans/{clanId}/join-requests", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isForbidden());
  }

  // ---- approve ----

  @Test
  void approve_asLeader_returns200() throws Exception {
    doNothing().when(joinRequestService).approveRequest(any(), any(), any());

    mockMvc
        .perform(
            post(
                    "/api/social/clans/{clanId}/join-requests/{requestId}/approve",
                    CLAN_ID,
                    REQUEST_ID)
                .principal(userPrincipal()))
        .andExpect(status().isOk());

    verify(joinRequestService).approveRequest(CLAN_ID, REQUEST_ID, USER_ID);
  }

  @Test
  void approve_asNonLeader_returns403() throws Exception {
    doThrow(new NotClanLeaderException("Not the leader"))
        .when(joinRequestService)
        .approveRequest(any(), any(), any());

    mockMvc
        .perform(
            post(
                    "/api/social/clans/{clanId}/join-requests/{requestId}/approve",
                    CLAN_ID,
                    REQUEST_ID)
                .principal(userPrincipal()))
        .andExpect(status().isForbidden());
  }

  @Test
  void approve_alreadyResolved_returns409() throws Exception {
    doThrow(new JoinRequestAlreadyResolvedException("Cannot approve request with status: APPROVED"))
        .when(joinRequestService)
        .approveRequest(any(), any(), any());

    mockMvc
        .perform(
            post(
                    "/api/social/clans/{clanId}/join-requests/{requestId}/approve",
                    CLAN_ID,
                    REQUEST_ID)
                .principal(userPrincipal()))
        .andExpect(status().isConflict());
  }

  // ---- reject ----

  @Test
  void reject_asLeader_returns200() throws Exception {
    doNothing().when(joinRequestService).rejectRequest(any(), any(), any());

    mockMvc
        .perform(
            post("/api/social/clans/{clanId}/join-requests/{requestId}/reject", CLAN_ID, REQUEST_ID)
                .principal(userPrincipal()))
        .andExpect(status().isOk());

    verify(joinRequestService).rejectRequest(CLAN_ID, REQUEST_ID, USER_ID);
  }

  @Test
  void reject_asNonLeader_returns403() throws Exception {
    doThrow(new NotClanLeaderException("Not the leader"))
        .when(joinRequestService)
        .rejectRequest(any(), any(), any());

    mockMvc
        .perform(
            post("/api/social/clans/{clanId}/join-requests/{requestId}/reject", CLAN_ID, REQUEST_ID)
                .principal(userPrincipal()))
        .andExpect(status().isForbidden());
  }
}
