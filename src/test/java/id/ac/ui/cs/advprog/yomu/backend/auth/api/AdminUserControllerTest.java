package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AdminUserController adminUserController;

  @Test
  void promoteShouldCallServiceAndReturn204() throws Exception {
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminUserController).build();
    UUID userId = UUID.randomUUID();

    mockMvc.perform(put("/api/admin/users/{id}/promote", userId)).andExpect(status().isNoContent());

    verify(authService).promoteToAdmin(userId);
  }

  @Test
  void demoteShouldCallServiceAndReturn204() throws Exception {
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminUserController).build();
    UUID userId = UUID.randomUUID();

    mockMvc.perform(put("/api/admin/users/{id}/demote", userId)).andExpect(status().isNoContent());

    verify(authService).demoteToUser(userId);
  }

  @Test
  void promoteShouldReturn400WhenUserNotFound() throws Exception {
    MockMvc mockMvc =
        MockMvcBuilders.standaloneSetup(adminUserController)
            .setControllerAdvice(new AuthExceptionHandler())
            .build();
    UUID userId = UUID.randomUUID();

    doThrow(new IllegalArgumentException("User not found"))
        .when(authService)
        .promoteToAdmin(userId);

    mockMvc
        .perform(put("/api/admin/users/{id}/promote", userId))
        .andExpect(status().isBadRequest());
  }

  @Test
  void demoteShouldReturn400WhenUserNotFound() throws Exception {
    MockMvc mockMvc =
        MockMvcBuilders.standaloneSetup(adminUserController)
            .setControllerAdvice(new AuthExceptionHandler())
            .build();
    UUID userId = UUID.randomUUID();

    doThrow(new IllegalArgumentException("User not found")).when(authService).demoteToUser(userId);

    mockMvc.perform(put("/api/admin/users/{id}/demote", userId)).andExpect(status().isBadRequest());
  }
}
