package id.ac.ui.cs.advprog.yomu.backend.social.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomu.backend.social.api.dto.LeaderboardEntryResponse;
import id.ac.ui.cs.advprog.yomu.backend.social.application.LeaderboardService;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LeaderboardControllerTest {

  @Mock private LeaderboardService leaderboardService;

  @InjectMocks private LeaderboardController leaderboardController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(leaderboardController).build();
  }

  @Test
  void getLeaderboard_noParam_usesBronzeDefault_returns200() throws Exception {
    when(leaderboardService.getLeaderboard(Tier.BRONZE)).thenReturn(List.of());

    mockMvc.perform(get("/api/social/leaderboard")).andExpect(status().isOk());

    verify(leaderboardService).getLeaderboard(Tier.BRONZE);
  }

  @Test
  void getLeaderboard_withTierParam_usesGivenTier_returns200() throws Exception {
    when(leaderboardService.getLeaderboard(Tier.DIAMOND)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/social/leaderboard").param("tier", "DIAMOND"))
        .andExpect(status().isOk());

    verify(leaderboardService).getLeaderboard(Tier.DIAMOND);
  }

  @Test
  void getLeaderboard_returnsEntriesInBody() throws Exception {
    UUID clanId = UUID.randomUUID();
    LeaderboardEntryResponse entry =
        new LeaderboardEntryResponse(1, clanId, "Alpha", 1200L, Tier.GOLD, true, false);
    when(leaderboardService.getLeaderboard(Tier.GOLD)).thenReturn(List.of(entry));

    mockMvc
        .perform(get("/api/social/leaderboard").param("tier", "GOLD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].rank").value(1))
        .andExpect(jsonPath("$[0].clanId").value(clanId.toString()))
        .andExpect(jsonPath("$[0].clanName").value("Alpha"))
        .andExpect(jsonPath("$[0].score").value(1200))
        .andExpect(jsonPath("$[0].tier").value("GOLD"))
        .andExpect(jsonPath("$[0].buffActive").value(true))
        .andExpect(jsonPath("$[0].debuffActive").value(false));
  }
}
