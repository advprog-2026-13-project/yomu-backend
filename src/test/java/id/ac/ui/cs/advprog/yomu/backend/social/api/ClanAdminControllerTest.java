package id.ac.ui.cs.advprog.yomu.backend.social.api;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.social.application.ClanService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.JoinRequestRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.UserLookupPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMemberRole;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ClanAdminControllerTest {

  @Mock private ClanRepositoryPort clanRepository;
  @Mock private ClanMemberRepositoryPort clanMemberRepository;
  @Mock private JoinRequestRepositoryPort joinRequestRepository;
  @Mock private ClanService clanService;
  @Mock private UserLookupPort userLookup;

  @InjectMocks private ClanAdminController controller;

  private MockMvc mockMvc;

  private final UUID clanId   = UUID.randomUUID();
  private final UUID leaderId = UUID.randomUUID();
  private final UUID memberId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new SocialExceptionHandler())
            .build();

    User admin = new User("admin", "Admin", "admin@mail.com", "0800", "hashed", Role.ADMIN);
    SecurityUser adminUser = new SecurityUser(admin);
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities()));
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // ── listClans ──────────────────────────────────────────────────────────────

  @Test
  void listClans_returnsOkWithClanList() throws Exception {
    Clan clan = new Clan();
    clan.setId(clanId);
    clan.setName("Test Clan");
    clan.setTier(Tier.GOLD);
    clan.setScore(1000L);
    clan.setLeaderId(leaderId);

    when(clanRepository.findAll()).thenReturn(List.of(clan));
    when(clanMemberRepository.countByClanId(clanId)).thenReturn(2L);

    mockMvc.perform(get("/api/admin/social/clans"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test Clan"))
        .andExpect(jsonPath("$[0].tier").value("GOLD"));
  }

  // ── listMembers ────────────────────────────────────────────────────────────

  @Test
  void listMembers_returnsUsernameResolvedFromPort() throws Exception {
    ClanMember leader = new ClanMember(UUID.randomUUID(), clanId, leaderId, ClanMemberRole.LEADER, Instant.now());
    ClanMember member = new ClanMember(UUID.randomUUID(), clanId, memberId, ClanMemberRole.MEMBER, Instant.now());

    when(clanMemberRepository.findByClanId(clanId)).thenReturn(List.of(leader, member));
    when(userLookup.findUsernameById(leaderId)).thenReturn(Optional.of("budi_elite"));
    when(userLookup.findUsernameById(memberId)).thenReturn(Optional.of("agus_baca"));

    mockMvc.perform(get("/api/admin/social/clans/{clanId}/members", clanId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("budi_elite"))
        .andExpect(jsonPath("$[0].role").value("LEADER"))
        .andExpect(jsonPath("$[1].username").value("agus_baca"))
        .andExpect(jsonPath("$[1].role").value("MEMBER"));
  }

  @Test
  void listMembers_whenUserNotFound_returnsNullUsername() throws Exception {
    ClanMember member = new ClanMember(UUID.randomUUID(), clanId, memberId, ClanMemberRole.MEMBER, Instant.now());

    when(clanMemberRepository.findByClanId(clanId)).thenReturn(List.of(member));
    when(userLookup.findUsernameById(memberId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/admin/social/clans/{clanId}/members", clanId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").doesNotExist());
  }

  // ── deleteClan ─────────────────────────────────────────────────────────────

  @Test
  void deleteClan_returnsNoContent() throws Exception {
    doNothing().when(clanService).adminDeleteClan(clanId);

    mockMvc.perform(delete("/api/admin/social/clans/{clanId}", clanId))
        .andExpect(status().isNoContent());

    verify(clanService).adminDeleteClan(clanId);
  }

  // ── removeMember ───────────────────────────────────────────────────────────

  @Test
  void removeMember_returnsNoContent() throws Exception {
    doNothing().when(clanService).adminRemoveMember(clanId, memberId);

    mockMvc.perform(delete("/api/admin/social/clans/{clanId}/members/{userId}", clanId, memberId))
        .andExpect(status().isNoContent());

    verify(clanService).adminRemoveMember(clanId, memberId);
  }

  // ── listJoinRequests ───────────────────────────────────────────────────────

  @Test
  void listJoinRequests_returnsOk() throws Exception {
    JoinRequest req = JoinRequest.create(clanId, memberId);
    when(joinRequestRepository.findAll()).thenReturn(List.of(req));

    mockMvc.perform(get("/api/admin/social/join-requests"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("PENDING"));
  }
}
