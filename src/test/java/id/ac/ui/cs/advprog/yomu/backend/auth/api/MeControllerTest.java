package id.ac.ui.cs.advprog.yomu.backend.auth.api;

// Import static dari Factory dan MockMvc
import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.UpdateAccountRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class MeControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private MeController meController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(meController).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  void meShouldReturnOk() throws Exception {
    // GANTI: Gunakan Factory
    User user = createDummyUser();
    MeResponse response = createMeResponse(user);

    when(authService.me()).thenReturn(response);

    mockMvc
        .perform(get("/api/auth/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
        .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
        .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));

    verify(authService).me();
  }

  @Test
  void updateAccountShouldReturnOk() throws Exception {
    UpdateAccountRequest request = new UpdateAccountRequest();
    request.setDisplayName("New Name");

    User user = createDummyUser();

    MeResponse response =
        new MeResponse(
            user.getId(),
            user.getUsername(),
            "New Name",
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole());

    when(authService.updateAccount(any(UpdateAccountRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            patch("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("New Name"));

    verify(authService).updateAccount(any(UpdateAccountRequest.class));
  }

  @Test
  void deleteAccountShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/auth/me")).andExpect(status().isNoContent());

    verify(authService).deleteAccount();
  }
}
