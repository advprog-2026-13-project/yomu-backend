package id.ac.ui.cs.advprog.yomu.backend.forum.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in.ForumUseCase;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ForumAdminControllerTest {

  @Mock private ForumUseCase forumUseCase;

  @InjectMocks private ForumAdminController forumAdminController;

  private MockMvc mockMvc;
  private UUID adminId;
  private UUID commentId;
  private SecurityUser adminSecurityUser;

  @BeforeEach
  @SuppressWarnings("unused")
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(forumAdminController)
            .setControllerAdvice(new ForumExceptionHandler())
            .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
            .build();

    adminId = UUID.randomUUID();
    commentId = UUID.randomUUID();

    User admin = new User("admin", "Admin", "admin@mail.com", "0800", "hashed", Role.ADMIN);
    admin.setId(adminId);
    adminSecurityUser = new SecurityUser(admin);

    // Make @AuthenticationPrincipal work in standaloneSetup by populating SecurityContextHolder.
    SecurityContextHolder.getContext().setAuthentication(adminPrincipal());
  }

  @AfterEach
  @SuppressWarnings("unused")
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private UsernamePasswordAuthenticationToken adminPrincipal() {
    return new UsernamePasswordAuthenticationToken(
        adminSecurityUser, null, adminSecurityUser.getAuthorities());
  }

  // ─── DELETE /api/admin/forums/comments/{id} ─────────────────────

  @Test
  void moderateDeleteShouldReturn204() throws Exception {
    doNothing().when(forumUseCase).deleteComment(eq(commentId), eq(adminId), eq(true));

    mockMvc
        .perform(delete("/api/admin/forums/comments/{id}", commentId).principal(adminPrincipal()))
        .andExpect(status().isNoContent());

    verify(forumUseCase).deleteComment(commentId, adminId, true);
  }

  @Test
  void moderateDeleteShouldReturn404WhenCommentNotFound() throws Exception {
    doThrow(new ForumNotFoundException("Comment not found"))
        .when(forumUseCase)
        .deleteComment(any(), any(), eq(true));

    mockMvc
        .perform(delete("/api/admin/forums/comments/{id}", commentId).principal(adminPrincipal()))
        .andExpect(status().isNotFound());
  }

  @Test
  void moderateDeleteShouldPassIsAdminTrueToService() throws Exception {
    doNothing().when(forumUseCase).deleteComment(any(), any(), anyBoolean());

    mockMvc
        .perform(delete("/api/admin/forums/comments/{id}", commentId).principal(adminPrincipal()))
        .andExpect(status().isNoContent());

    verify(forumUseCase).deleteComment(eq(commentId), eq(adminId), eq(true));
  }
}
