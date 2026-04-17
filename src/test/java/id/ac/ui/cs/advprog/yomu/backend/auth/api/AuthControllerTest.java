package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import java.util.UUID;
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
class AuthControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  void registerShouldReturnOkWhenRequestIsValid() throws Exception {
    UUID id = UUID.randomUUID();
    RegisterRequest request =
        new RegisterRequest("rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", "secret123");
    MeResponse response =
        new MeResponse(id, "rifqi", "Rifqi Ahmad", "rifqi@mail.com", "08123", Role.USER);

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.username").value("rifqi"))
        .andExpect(jsonPath("$.displayName").value("Rifqi Ahmad")) // Verifikasi field baru
        .andExpect(jsonPath("$.email").value("rifqi@mail.com"))
        .andExpect(jsonPath("$.phoneNumber").value("08123")) // Verifikasi field baru
        .andExpect(jsonPath("$.role").value("USER"));

    verify(authService).register(any(RegisterRequest.class));
  }

  @Test
  void loginShouldReturnOkWhenRequestIsValid() throws Exception {
    LoginRequest request = new LoginRequest("rifqi", "secret123");
    AuthResponse response = new AuthResponse("dummy-jwt-token");

    when(authService.login(any(LoginRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("dummy-jwt-token"));

    verify(authService).login(any(LoginRequest.class));
  }
}
