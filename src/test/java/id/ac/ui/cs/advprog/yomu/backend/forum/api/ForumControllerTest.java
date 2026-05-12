package id.ac.ui.cs.advprog.yomu.backend.forum.api;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.EditCommentRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.PostCommentRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.ReactRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.ReplyRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumBadRequestException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumForbiddenException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in.ForumUseCase;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.UserSummary;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;

@ExtendWith(MockitoExtension.class)
class ForumControllerTest {

  @Mock private ForumUseCase forumUseCase;

  @InjectMocks private ForumController forumController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  private UUID userId;
  private UUID readingId;
  private UUID commentId;
  private SecurityUser securityUser;

  @BeforeEach
  @SuppressWarnings("unused")
  void setUp() {
    mockMvc =
      MockMvcBuilders.standaloneSetup(forumController)
        .setControllerAdvice(new ForumExceptionHandler())
        .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
        .build();
    objectMapper = new ObjectMapper();

    userId = UUID.randomUUID();
    readingId = UUID.randomUUID();
    commentId = UUID.randomUUID();

    User user = new User("bob", "Bob", "bob@mail.com", "0812", "hashed", Role.USER);
    user.setId(userId);
    securityUser = new SecurityUser(user);

    // Make @AuthenticationPrincipal work in standaloneSetup by populating SecurityContextHolder.
    SecurityContextHolder.getContext().setAuthentication(principal());
  }

  @AfterEach
  @SuppressWarnings("unused")
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // ─── helper ─────────────────────────────────────────────────────

  private CommentView buildView(UUID id, UUID reading, UUID parent) {
    return new CommentView(
        id, reading,
        new UserSummary(userId, "Bob"),
        parent, "Test content", false,
        Instant.now(), null,
        Map.of(), Collections.emptyList());
  }

  private UsernamePasswordAuthenticationToken principal() {
    return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
  }

  // ─── GET /api/forums/{readingId}/comments ───────────────────────

  @Test
  void getCommentsShouldReturnOkWithList() throws Exception {
    CommentView view = buildView(commentId, readingId, null);
    when(forumUseCase.getComments(readingId)).thenReturn(List.of(view));

    mockMvc.perform(get("/api/forums/{readingId}/comments", readingId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(commentId.toString()))
        .andExpect(jsonPath("$[0].content").value("Test content"));
  }

  @Test
  void getCommentsShouldReturnEmptyList() throws Exception {
    when(forumUseCase.getComments(readingId)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/forums/{readingId}/comments", readingId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }

  // ─── POST /api/forums/{readingId}/comments ──────────────────────

  @Test
  void postCommentShouldReturn201WithCreatedView() throws Exception {
    CommentView view = buildView(commentId, readingId, null);
    when(forumUseCase.postComment(eq(readingId), eq(userId), eq("Hello!")))
        .thenReturn(view);

    PostCommentRequest request = new PostCommentRequest("Hello!");

    mockMvc.perform(post("/api/forums/{readingId}/comments", readingId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(commentId.toString()));
  }

  @Test
  void postCommentShouldReturn400WhenContentIsBlank() throws Exception {
    PostCommentRequest request = new PostCommentRequest("");

    // Validation should fail before calling the service.
    mockMvc.perform(post("/api/forums/{readingId}/comments", readingId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(forumUseCase, never()).postComment(any(), any(), any());
  }

  // ─── POST /api/forums/comments/{id}/replies ─────────────────────

  @Test
  void replyToCommentShouldReturn201WithReplyView() throws Exception {
    UUID replyId = UUID.randomUUID();
    CommentView view = buildView(replyId, readingId, commentId);
    when(forumUseCase.replyToComment(eq(commentId), eq(userId), eq("I agree")))
        .thenReturn(view);

    ReplyRequest request = new ReplyRequest("I agree");

    mockMvc.perform(post("/api/forums/comments/{id}/replies", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.parentId").value(commentId.toString()));
  }

  @Test
  void replyToCommentShouldReturn404WhenParentNotFound() throws Exception {
    when(forumUseCase.replyToComment(any(), any(), any()))
        .thenThrow(new ForumNotFoundException("Parent comment not found"));

    ReplyRequest request = new ReplyRequest("reply");

    mockMvc.perform(post("/api/forums/comments/{id}/replies", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  // ─── PUT /api/forums/comments/{id} ──────────────────────────────

  @Test
  void editCommentShouldReturn204() throws Exception {
    doNothing().when(forumUseCase).editComment(eq(commentId), eq(userId), eq("Updated"));

    EditCommentRequest request = new EditCommentRequest("Updated");

    mockMvc.perform(put("/api/forums/comments/{id}", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    verify(forumUseCase).editComment(commentId, userId, "Updated");
  }

  @Test
  void editCommentShouldReturn403WhenNotAuthor() throws Exception {
    doThrow(new ForumForbiddenException("Only the author can edit this comment"))
        .when(forumUseCase).editComment(any(), any(), any());

    EditCommentRequest request = new EditCommentRequest("Updated");

    mockMvc.perform(put("/api/forums/comments/{id}", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }

  // ─── DELETE /api/forums/comments/{id} ───────────────────────────

  @Test
  void deleteCommentShouldReturn204() throws Exception {
    doNothing().when(forumUseCase).deleteComment(eq(commentId), eq(userId), eq(false));

    mockMvc.perform(delete("/api/forums/comments/{id}", commentId)
            .principal(principal()))
        .andExpect(status().isNoContent());

    verify(forumUseCase).deleteComment(commentId, userId, false);
  }

  @Test
  void deleteCommentShouldReturn403WhenNotAuthor() throws Exception {
    doThrow(new ForumForbiddenException("You do not have permission"))
        .when(forumUseCase).deleteComment(any(), any(), anyBoolean());

    mockMvc.perform(delete("/api/forums/comments/{id}", commentId)
            .principal(principal()))
        .andExpect(status().isForbidden());
  }

  @Test
  void deleteCommentShouldReturn404WhenNotFound() throws Exception {
    doThrow(new ForumNotFoundException("Comment not found"))
        .when(forumUseCase).deleteComment(any(), any(), anyBoolean());

    mockMvc.perform(delete("/api/forums/comments/{id}", commentId)
            .principal(principal()))
        .andExpect(status().isNotFound());
  }

  // ─── POST /api/forums/comments/{id}/reactions ───────────────────

  @Test
  void reactShouldReturn204() throws Exception {
    doNothing().when(forumUseCase).toggleReaction(eq(commentId), eq(userId), eq(ReactionType.UPVOTE));

    ReactRequest request = new ReactRequest("UPVOTE");

    mockMvc.perform(post("/api/forums/comments/{id}/reactions", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());

    verify(forumUseCase).toggleReaction(commentId, userId, ReactionType.UPVOTE);
  }

  @Test
  void reactShouldReturn400WhenReactionTypeIsInvalid() throws Exception {
    ReactRequest request = new ReactRequest("INVALID_TYPE");

    mockMvc.perform(post("/api/forums/comments/{id}/reactions", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());

    verify(forumUseCase, never()).toggleReaction(any(), any(), any());
  }

  @Test
  void reactShouldReturn400WhenCommentIsDeleted() throws Exception {
    doThrow(new ForumBadRequestException("Cannot react to a deleted comment"))
        .when(forumUseCase).toggleReaction(any(), any(), any());

    ReactRequest request = new ReactRequest("UPVOTE");

    mockMvc.perform(post("/api/forums/comments/{id}/reactions", commentId)
            .principal(principal())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
