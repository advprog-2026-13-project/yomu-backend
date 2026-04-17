package id.ac.ui.cs.advprog.yomu.backend.auth.api;

// Import static dari Factory dan MockMvc
import static id.ac.ui.cs.advprog.yomu.backend.auth.TestDataFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.RegisterRequest;
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
    RegisterRequest request = createRegisterRequest();
    User dummyUser = createDummyUser();

    MeResponse response =
        new MeResponse(
            dummyUser.getId(),
            dummyUser.getUsername(),
            dummyUser.getDisplayName(),
            dummyUser.getEmail(),
            dummyUser.getPhoneNumber(),
            dummyUser.getRole());

    when(authService.register(any(RegisterRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
        .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
        .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
        .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE));

    verify(authService).register(any(RegisterRequest.class));
  }

  @Test
  void loginShouldReturnOkWhenRequestIsValid() throws Exception {
    LoginRequest request = new LoginRequest(DEFAULT_USERNAME, DEFAULT_PASSWORD);
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

  @Test
  void googleLoginShouldReturnOk() throws Exception {
    String mockToken = "some-google-token";
    AuthResponse response = new AuthResponse("google-jwt-token");

    // Pastikan Mockito menangkap argumen yang tepat
    when(authService.loginWithGoogle(mockToken)).thenReturn(response);

    // Kirim JSON dengan key "token" (sesuai request.get("token") di Controller)
    java.util.Map<String, String> body = java.util.Map.of("token", mockToken);

    mockMvc
        .perform(
            post("/api/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andDo(print()) // Membantu debug jika ada error lagi
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("google-jwt-token"));

    verify(authService).loginWithGoogle(mockToken);
  }
}
