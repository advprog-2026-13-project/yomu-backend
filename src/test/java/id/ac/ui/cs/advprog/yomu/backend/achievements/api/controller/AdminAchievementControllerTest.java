package id.ac.ui.cs.advprog.yomu.backend.achievements.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto.CreateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto.CreateDailyMissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto.UpdateAchievementRequest;
import id.ac.ui.cs.advprog.yomu.backend.achievements.api.dto.UpdateDailyMissionRequest;
import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementAdminService;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.Achievement;
import id.ac.ui.cs.advprog.yomu.backend.achievements.domain.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.backend.achievements.events.envelope.AchievementType;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminAchievementControllerTest {

  private MockMvc mockMvc;

  @Mock private AchievementAdminService adminService;

  @InjectMocks private AdminAchievementController controller;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void testCreateAchievement() throws Exception {
    CreateAchievementRequest request =
        new CreateAchievementRequest("Test", "Desc", AchievementType.READING_COMPLETED, 10);
    Achievement achievement =
        new Achievement(UUID.randomUUID(), "Test", "Desc", AchievementType.READING_COMPLETED, 10);

    when(adminService.createAchievement(
            anyString(), anyString(), any(AchievementType.class), anyInt()))
        .thenReturn(achievement);

    mockMvc
        .perform(
            post("/api/admin/achievements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test"))
        .andExpect(jsonPath("$.milestone").value(10));
  }

  @Test
  void testUpdateAchievementSuccess() throws Exception {
    UUID id = UUID.randomUUID();
    UpdateAchievementRequest request = new UpdateAchievementRequest("Updated", "New Desc", 20);
    Achievement updated =
        new Achievement(id, "Updated", "New Desc", AchievementType.READING_COMPLETED, 20);

    when(adminService.updateAchievement(any(UUID.class), anyString(), anyString(), anyInt()))
        .thenReturn(updated);

    mockMvc
        .perform(
            put("/api/admin/achievements/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"))
        .andExpect(jsonPath("$.description").value("New Desc"))
        .andExpect(jsonPath("$.milestone").value(20));
  }

  @Test
  void testUpdateAchievementNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    UpdateAchievementRequest request = new UpdateAchievementRequest("Updated", "Desc", 10);

    when(adminService.updateAchievement(any(UUID.class), anyString(), anyString(), anyInt()))
        .thenThrow(new NoSuchElementException("Achievement not found"));

    mockMvc
        .perform(
            put("/api/admin/achievements/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteAchievement() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc.perform(delete("/api/admin/achievements/" + id)).andExpect(status().isNoContent());
  }

  @Test
  void testCreateDailyMission() throws Exception {
    CreateDailyMissionRequest request =
        new CreateDailyMissionRequest("Mission", "Desc", AchievementType.QUIZ_COMPLETED, 5);
    DailyMission mission =
        new DailyMission(UUID.randomUUID(), "Mission", "Desc", AchievementType.QUIZ_COMPLETED, 5);

    when(adminService.createDailyMission(
            anyString(), anyString(), any(AchievementType.class), anyInt()))
        .thenReturn(mission);

    mockMvc
        .perform(
            post("/api/admin/achievements/daily-missions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Mission"))
        .andExpect(jsonPath("$.milestone").value(5));
  }

  @Test
  void testUpdateDailyMissionSuccess() throws Exception {
    UUID id = UUID.randomUUID();
    UpdateDailyMissionRequest request = new UpdateDailyMissionRequest("Updated Mission", "New Desc", 15);
    DailyMission updated =
        new DailyMission(id, "Updated Mission", "New Desc", AchievementType.QUIZ_COMPLETED, 15);

    when(adminService.updateDailyMission(any(UUID.class), anyString(), anyString(), anyInt()))
        .thenReturn(updated);

    mockMvc
        .perform(
            put("/api/admin/achievements/daily-missions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Mission"))
        .andExpect(jsonPath("$.description").value("New Desc"))
        .andExpect(jsonPath("$.milestone").value(15));
  }

  @Test
  void testUpdateDailyMissionNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    UpdateDailyMissionRequest request = new UpdateDailyMissionRequest("Updated", "Desc", 10);

    when(adminService.updateDailyMission(any(UUID.class), anyString(), anyString(), anyInt()))
        .thenThrow(new NoSuchElementException("Daily mission not found"));

    mockMvc
        .perform(
            put("/api/admin/achievements/daily-missions/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteDailyMission() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc
        .perform(delete("/api/admin/achievements/daily-missions/" + id))
        .andExpect(status().isNoContent());
  }
}

