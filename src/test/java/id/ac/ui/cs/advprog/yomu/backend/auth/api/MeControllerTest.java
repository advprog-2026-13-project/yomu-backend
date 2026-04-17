package id.ac.ui.cs.advprog.yomu.backend.auth.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.application.AuthService;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
        SecurityContextHolder.clearContext();
    }

    @Test
    void meShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User("rifqi", "Rifqi Ilham", "rifqi@mail.com", "08123", "hashed", Role.USER);
        user.setId(id);
        
        MeResponse response = new MeResponse(id, "rifqi", "Rifqi Ilham", "rifqi@mail.com", "08123", Role.USER);
        
        when(authService.me()).thenReturn(response);

        mockMvc
            .perform(get("/api/auth/me")) 
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.username").value("rifqi"))
            .andExpect(jsonPath("$.displayName").value("Rifqi Ilham"))
            .andExpect(jsonPath("$.email").value("rifqi@mail.com"))
            .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).me();
    }
}