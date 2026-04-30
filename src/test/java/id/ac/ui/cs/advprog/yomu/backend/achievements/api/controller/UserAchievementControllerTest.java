package id.ac.ui.cs.advprog.yomu.backend.achievements.api.controller;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementQueryService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.UserDailyMissionProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAchievementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AchievementQueryService queryService;

    @InjectMocks
    private UserAchievementController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetAllAchievements() throws Exception {
        Achievement achievement = mock(Achievement.class);
        when(achievement.getName()).thenReturn("Test Achievement");
        when(queryService.getAllAchievements()).thenReturn(List.of(achievement));

        mockMvc.perform(get("/api/achievements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Achievement"));
    }

    @Test
    void testGetAllDailyMissions() throws Exception {
        DailyMission mission = mock(DailyMission.class);
        when(mission.getName()).thenReturn("Test Mission");
        when(queryService.getAllDailyMissions()).thenReturn(List.of(mission));

        mockMvc.perform(get("/api/achievements/daily-missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Mission"));
    }

    @Test
    void testGetUserAchievementProgress() throws Exception {
        UUID userId = UUID.randomUUID();
        UserAchievementProgress progress = mock(UserAchievementProgress.class);
        when(progress.getCurrentProgress()).thenReturn(5);
        when(queryService.getUserAchievementProgress(userId)).thenReturn(List.of(progress));

        mockMvc.perform(get("/api/achievements/users/" + userId + "/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentProgress").value(5));
    }

    @Test
    void testGetUserDailyMissionProgress() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDailyMissionProgress progress = mock(UserDailyMissionProgress.class);
        when(progress.getCurrentProgress()).thenReturn(2);
        when(queryService.getUserDailyMissionProgressForToday(userId)).thenReturn(List.of(progress));

        mockMvc.perform(get("/api/achievements/users/" + userId + "/daily-progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentProgress").value(2));
    }

    @Test
    void testGetCompletedAchievements() throws Exception {
        UUID userId = UUID.randomUUID();
        UserAchievementProgress progress = mock(UserAchievementProgress.class);
        when(progress.isCompleted()).thenReturn(true);
        when(queryService.getCompletedAchievements(userId)).thenReturn(List.of(progress));

        mockMvc.perform(get("/api/achievements/users/" + userId + "/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed").value(true));
    }
}
