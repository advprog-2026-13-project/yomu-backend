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

  private GoogleIdTokenVerifier verifier;

  public GoogleService() {}

  public GoogleIdToken.Payload verifyToken(String idTokenString) {
    if (this.verifier == null) {
      this.verifier =
          new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
              .setAudience(Collections.singletonList(googleClientId))
              .build();
    }

    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);
      if (idToken != null) {
        return idToken.getPayload();
      }
    } catch (Exception e) {
    }
    return null;
  }
}
