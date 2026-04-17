package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {

  @Value("${app.google.client-id}")
  private String googleClientId;

  public GoogleIdToken.Payload verifyToken(String idTokenString) {
    try {
      GoogleIdTokenVerifier verifier =
          new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
              .setAudience(Collections.singletonList(googleClientId))
              .build();

      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {
        return idToken.getPayload();
      }
    } catch (Exception e) {
      System.err.println("Google Verification Error: " + e.getMessage());
    }
    return null;
  }
}
