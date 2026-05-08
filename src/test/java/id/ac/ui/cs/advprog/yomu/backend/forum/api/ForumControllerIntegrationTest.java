package id.ac.ui.cs.advprog.yomu.backend.forum.api;

import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ForumControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  @Transactional
  void forumFlow_postReplyReactToggle_getCommentsTreeAndCounts() throws Exception {
    String password = "secret123";
    String token =
        registerAndLogin(
            "alice_" + randomSuffix(), "Alice", "alice_" + randomSuffix() + "@mail.com", password);

    UUID readingId = UUID.randomUUID();

    UUID rootCommentId = postComment(readingId, token, "Root comment");
    replyToComment(rootCommentId, token, "First reply");

    // toggle: add then remove
    react(rootCommentId, token, "EMOJI_LIKE");
    react(rootCommentId, token, "EMOJI_LIKE");

    // multi-emoji (independent)
    react(rootCommentId, token, "EMOJI_LAUGH");
    react(rootCommentId, token, "EMOJI_WOW");

    // votes (mutually exclusive)
    react(rootCommentId, token, "UPVOTE");
    react(rootCommentId, token, "DOWNVOTE");

    mockMvc
        .perform(
            get("/api/forums/{readingId}/comments", readingId)
                .header("Authorization", bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(rootCommentId.toString()))
        .andExpect(jsonPath("$[0].readingId").value(readingId.toString()))
        .andExpect(jsonPath("$[0].content").value("Root comment"))
        .andExpect(jsonPath("$[0].deleted").value(false))
        .andExpect(jsonPath("$[0].replies.length()").value(1))
        .andExpect(jsonPath("$[0].replies[0].content").value("First reply"))
        .andExpect(jsonPath("$[0].reactionCounts.EMOJI_LAUGH").value(1))
        .andExpect(jsonPath("$[0].reactionCounts.EMOJI_WOW").value(1))
        .andExpect(jsonPath("$[0].reactionCounts.DOWNVOTE").value(1))
        .andExpect(jsonPath("$[0].reactionCounts.EMOJI_LIKE").doesNotExist())
        .andExpect(jsonPath("$[0].reactionCounts.UPVOTE").doesNotExist());
  }

  @Test
  @Transactional
  void forumFlow_nonAuthorCannotEditOrDelete() throws Exception {
    String password = "secret123";

    String aliceToken =
        registerAndLogin(
            "alice_" + randomSuffix(), "Alice", "alice_" + randomSuffix() + "@mail.com", password);
    String bobToken =
        registerAndLogin(
            "bob_" + randomSuffix(), "Bob", "bob_" + randomSuffix() + "@mail.com", password);

    UUID readingId = UUID.randomUUID();
    UUID commentId = postComment(readingId, aliceToken, "Hello");

    mockMvc
        .perform(
            put("/api/forums/comments/{id}", commentId)
                .header("Authorization", bearer(bobToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newContent\":\"Hacked\"}"))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(
            delete("/api/forums/comments/{id}", commentId)
                .header("Authorization", bearer(bobToken)))
        .andExpect(status().isForbidden());
  }

  @Test
  @Transactional
  void forumFlow_adminCanModerateDelete() throws Exception {
    String password = "secret123";

    String aliceToken =
        registerAndLogin(
            "alice_" + randomSuffix(), "Alice", "alice_" + randomSuffix() + "@mail.com", password);

    String adminUsername = "admin_" + randomSuffix();
    String adminPassword = "adminpass123";

    User admin =
        new User(
            adminUsername,
            "Admin",
            adminUsername + "@mail.com",
            null,
            passwordEncoder.encode(adminPassword),
            Role.ADMIN);
    userRepository.save(admin);

    String adminToken = login(adminUsername, adminPassword);

    UUID readingId = UUID.randomUUID();
    UUID commentId = postComment(readingId, aliceToken, "Will be moderated");

    mockMvc
        .perform(
            delete("/api/admin/forums/comments/{id}", commentId)
                .header("Authorization", bearer(adminToken)))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get("/api/forums/{readingId}/comments", readingId)
                .header("Authorization", bearer(aliceToken)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(commentId.toString()))
        .andExpect(jsonPath("$[0].deleted").value(true))
        .andExpect(jsonPath("$[0].content").value(nullValue()));
  }

  private UUID postComment(UUID readingId, String token, String content) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/api/forums/{readingId}/comments", readingId)
                    .header("Authorization", bearer(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"content\":" + objectMapper.writeValueAsString(content) + "}"))
            .andExpect(status().isCreated())
            .andReturn();

    JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
    return UUID.fromString(node.get("id").asText());
  }

  private void replyToComment(UUID commentId, String token, String content) throws Exception {
    mockMvc
        .perform(
            post("/api/forums/comments/{id}/replies", commentId)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":" + objectMapper.writeValueAsString(content) + "}"))
        .andExpect(status().isCreated());
  }

  private void react(UUID commentId, String token, String type) throws Exception {
    mockMvc
        .perform(
            post("/api/forums/comments/{id}/reactions", commentId)
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\":" + objectMapper.writeValueAsString(type) + "}"))
        .andExpect(status().isNoContent());
  }

  private String registerAndLogin(String username, String displayName, String email, String password)
      throws Exception {
    String registerBody =
        "{"
            + "\"username\":"
            + objectMapper.writeValueAsString(username)
            + ","
            + "\"displayName\":"
            + objectMapper.writeValueAsString(displayName)
            + ","
            + "\"email\":"
            + objectMapper.writeValueAsString(email)
            + ","
            + "\"phoneNumber\":null,"
            + "\"password\":"
            + objectMapper.writeValueAsString(password)
            + "}";

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
        .andExpect(status().isOk());

    return login(username, password);
  }

  private String login(String identifier, String password) throws Exception {
    String loginBody =
        "{"
            + "\"identifier\":"
            + objectMapper.writeValueAsString(identifier)
            + ","
            + "\"password\":"
            + objectMapper.writeValueAsString(password)
            + "}";

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginBody))
            .andExpect(status().isOk())
            .andReturn();

    JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
    return node.get("accessToken").asText();
  }

  private static String bearer(String token) {
    return "Bearer " + token;
  }

  private static String randomSuffix() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
