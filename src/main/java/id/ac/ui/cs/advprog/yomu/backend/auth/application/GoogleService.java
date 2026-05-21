package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleService {

  private static final Logger log = LoggerFactory.getLogger(GoogleService.class);

  @Value("${app.google.client-id}")
  private String googleClientId;

  private volatile GoogleIdTokenVerifier verifier;

  public GoogleIdToken.Payload verifyToken(String idTokenString) {
    GoogleIdTokenVerifier v = verifier;
    if (v == null) {
      synchronized (this) {
        v = verifier;
        if (v == null) {
          v =
              new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                  .setAudience(Collections.singletonList(googleClientId))
                  .build();
          verifier = v;
        }
      }
    }

    try {
      GoogleIdToken idToken = v.verify(idTokenString);
      if (idToken != null) {
        return idToken.getPayload();
      }
    } catch (Exception e) {
      log.warn("Google token verification failed: {}", e.getMessage());
    }
    return new GoogleIdToken.Payload();
  }
}
