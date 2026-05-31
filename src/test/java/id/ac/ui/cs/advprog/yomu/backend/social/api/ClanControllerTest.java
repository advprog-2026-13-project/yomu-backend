package id.ac.ui.cs.advprog.yomu.backend.social.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.ClanService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.UserLookupPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.AlreadyInClanException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.ClanNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.DuplicateClanNameException;
import id.ac.ui.cs.advprog.yomu.backend.social.application.exception.NotClanLeaderException;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private UserLookupPort userLookup;

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


  @Test
  void createClan_happyPath_returns201() throws Exception {
    Clan clan = Clan.createNew("Vipers", USER_ID);
    clan.setId(CLAN_ID);
    when(clanService.createClan(any(), any())).thenReturn(new ClanResponse(clan, 1L));

    mockMvc
        .perform(
            post("/api/social/clans")
                .principal(userPrincipal())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Vipers\"}"))
        .andExpect(status().isCreated());

    verify(clanService).createClan("Vipers", USER_ID);
  }

  @Test
  void createClan_duplicateName_returns409() throws Exception {
    doThrow(new DuplicateClanNameException("Clan name is already taken"))
        .when(clanService)
        .createClan(any(), any());

    mockMvc
        .perform(
            post("/api/social/clans")
                .principal(userPrincipal())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Vipers\"}"))
        .andExpect(status().isConflict());
  }

  @Test
  void createClan_userAlreadyInClan_returns409() throws Exception {
    doThrow(new AlreadyInClanException("User is already a member of a clan"))
        .when(clanService)
        .createClan(any(), any());

    mockMvc
        .perform(
            post("/api/social/clans")
                .principal(userPrincipal())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Vipers\"}"))
        .andExpect(status().isConflict());
  }

  @Test
  void leaveClan_happyPath_returns204() throws Exception {
    doNothing().when(clanService).leaveClan(any());

    mockMvc
        .perform(delete("/api/social/clans/leave").principal(userPrincipal()))
        .andExpect(status().isNoContent());

    verify(clanService).leaveClan(USER_ID);
  }

  @Test
  void leaveClan_userNotInClan_returns404() throws Exception {
    doThrow(new ClanNotFoundException("User is not in a clan")).when(clanService).leaveClan(any());

    mockMvc
        .perform(delete("/api/social/clans/leave").principal(userPrincipal()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getClan_happyPath_returns200WithClanBody() throws Exception {
    Clan clan = Clan.createNew("Vipers", USER_ID);
    clan.setId(CLAN_ID);
    when(clanService.getClan(CLAN_ID)).thenReturn(new ClanResponse(clan, 3L));

    mockMvc
        .perform(get("/api/social/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isOk());

    verify(clanService).getClan(CLAN_ID);
  }

  @Test
  void getClan_notFound_returns404() throws Exception {
    doThrow(new ClanNotFoundException("Clan not found")).when(clanService).getClan(any());

    mockMvc
        .perform(get("/api/social/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getMyClan_userInClan_returns200() throws Exception {
    Clan clan = Clan.createNew("Vipers", USER_ID);
    clan.setId(CLAN_ID);
    when(clanService.getMyClan(USER_ID)).thenReturn(Optional.of(new ClanResponse(clan, 2L)));

    mockMvc
        .perform(get("/api/social/clans/me").principal(userPrincipal()))
        .andExpect(status().isOk());

    verify(clanService).getMyClan(USER_ID);
  }

  @Test
  void getMyClan_userNotInClan_returns404() throws Exception {
    when(clanService.getMyClan(USER_ID)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/social/clans/me").principal(userPrincipal()))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteClan_asLeader_returns204() throws Exception {
    doNothing().when(clanService).deleteClan(any(), any());

    mockMvc
        .perform(delete("/api/social/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isNoContent());

    verify(clanService).deleteClan(CLAN_ID, USER_ID);
  }

  @Test
  void deleteClan_asNonLeader_returns403() throws Exception {
    doThrow(new NotClanLeaderException("Only the clan leader can delete the clan"))
        .when(clanService)
        .deleteClan(any(), any());

    mockMvc
        .perform(delete("/api/social/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteClan_clanNotFound_returns404() throws Exception {
    doThrow(new ClanNotFoundException("Clan not found")).when(clanService).deleteClan(any(), any());

    mockMvc
        .perform(delete("/api/social/clans/{clanId}", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isNotFound());
  }

  @Test
  void getClanMembers_returns200WithUsernameList() throws Exception {
    Clan clan = Clan.createNew("Vipers", USER_ID);
    clan.setId(CLAN_ID);
    when(clanService.getClan(CLAN_ID)).thenReturn(new ClanResponse(clan, 2L));

    UUID memberId = UUID.randomUUID();
    ClanMember leader = new ClanMember(UUID.randomUUID(), CLAN_ID, USER_ID, ClanMemberRole.LEADER, Instant.now());
    ClanMember member = new ClanMember(UUID.randomUUID(), CLAN_ID, memberId, ClanMemberRole.MEMBER, Instant.now());
    when(clanMemberRepository.findByClanId(CLAN_ID)).thenReturn(List.of(leader, member));
    when(userLookup.findUsernameById(USER_ID)).thenReturn(Optional.of("leader_user"));
    when(userLookup.findUsernameById(memberId)).thenReturn(Optional.of("member_user"));

    mockMvc
        .perform(get("/api/social/clans/{clanId}/members", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("leader_user"))
        .andExpect(jsonPath("$[0].role").value("LEADER"))
        .andExpect(jsonPath("$[1].username").value("member_user"))
        .andExpect(jsonPath("$[1].role").value("MEMBER"));
  }

  @Test
  void getClanMembers_clanNotFound_returns404() throws Exception {
    doThrow(new ClanNotFoundException("Clan not found")).when(clanService).getClan(any());

    mockMvc
        .perform(get("/api/social/clans/{clanId}/members", CLAN_ID).principal(userPrincipal()))
        .andExpect(status().isNotFound());
  }
}
