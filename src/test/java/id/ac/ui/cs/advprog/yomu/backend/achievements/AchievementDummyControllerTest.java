package id.ac.ui.cs.advprog.yomu.backend.achievements;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AchievementDummyControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldInsertAndReturnTotalHits() throws Exception {
    mockMvc
        .perform(post("/api/achievements/dummy-hit"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.saved").value(true))
        .andExpect(jsonPath("$.event").value("DUMMY_ACHIEVEMENT_HIT"))
        .andExpect(jsonPath("$.totalHits", greaterThanOrEqualTo(1)));

    mockMvc
        .perform(get("/api/achievements/dummy-hit"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalHits", greaterThanOrEqualTo(1)));
  }
}
