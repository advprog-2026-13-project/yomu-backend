package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class GoogleServiceTest {
  private GoogleIdTokenVerifier verifier;
  private GoogleService googleService;

  @BeforeEach
  void setUp() {
    googleService = new GoogleService();
    verifier = mock(GoogleIdTokenVerifier.class);

    ReflectionTestUtils.setField(googleService, "verifier", verifier);
    ReflectionTestUtils.setField(googleService, "googleClientId", "test-id");
  }

  @Test
  void verifyTokenShouldReturnPayloadWhenValid() throws Exception {
    GoogleIdToken idToken = mock(GoogleIdToken.class);
    GoogleIdToken.Payload payload = new GoogleIdToken.Payload();

    when(verifier.verify(anyString())).thenReturn(idToken);
    when(idToken.getPayload()).thenReturn(payload);

    var result = googleService.verifyToken("valid-token");
    assertNotNull(result);
    assertEquals(payload, result);
  }

  @Test
  void verifyTokenShouldReturnNullWhenExceptionOccurs() throws Exception {
    when(verifier.verify(anyString())).thenThrow(new IOException());
    assertNull(googleService.verifyToken("error-token"));
  }
}
