package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
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
class MeControllerTest {

  @Mock private AuthService authService;

  @InjectMocks private MeController meController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(meController).build();
  }

  @Test
  void meShouldReturnOk() throws Exception {
    UUID id = UUID.randomUUID();
    MeResponse response = new MeResponse(id, "rifqi", "rifqi@mail.com", Role.USER);

    when(authService.me()).thenReturn(response);

    mockMvc
        .perform(get("/api/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.username").value("rifqi"))
        .andExpect(jsonPath("$.email").value("rifqi@mail.com"))
        .andExpect(jsonPath("$.role").value("USER"));

    verify(authService).me();
  }
}
