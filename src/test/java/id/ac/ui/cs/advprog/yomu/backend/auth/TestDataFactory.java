package id.ac.ui.cs.advprog.yomu.backend.auth;

import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.MeResponse;
import id.ac.ui.cs.advprog.yomu.backend.auth.api.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.Role;
import id.ac.ui.cs.advprog.yomu.backend.auth.domain.User;
import java.util.UUID;

public class TestDataFactory {
  public static final String DEFAULT_USERNAME = "rifqi";
  public static final String DEFAULT_DISPLAY_NAME = "Rifqi Ilham";
  public static final String DEFAULT_EMAIL = "rifqi@mail.com";
  public static final String DEFAULT_PHONE = "08123";
  public static final String DEFAULT_PASSWORD = "secret123";

  public static User createDummyUser() {
    User user =
        new User(
            DEFAULT_USERNAME,
            DEFAULT_DISPLAY_NAME,
            DEFAULT_EMAIL,
            DEFAULT_PHONE,
            "hashed-password",
            Role.USER);
    user.setId(UUID.randomUUID());
    return user;
  }

  public static RegisterRequest createRegisterRequest() {
    return new RegisterRequest(
        DEFAULT_USERNAME, DEFAULT_DISPLAY_NAME, DEFAULT_EMAIL, DEFAULT_PHONE, DEFAULT_PASSWORD);
  }

  public static MeResponse createMeResponse(User user) {
    return new MeResponse(
        user.getId(),
        user.getUsername(),
        user.getDisplayName(),
        user.getEmail(),
        user.getPhoneNumber(),
        user.getRole());
  }
}
