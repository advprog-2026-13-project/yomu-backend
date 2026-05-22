package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;
import java.util.List;
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
  @Mock private UserRepository userRepository;

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

  @Test
  void listUsersShouldReturn200() throws Exception {
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminUserController).build();
    User user = new User("bob", "Bob", "bob@mail.com", "0812", "hash", Role.USER);
    user.setId(UUID.randomUUID());

    when(userRepository.findAll()).thenReturn(List.of(user));

    mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk());
  }
}
