package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

  private UUID id;
  private UUID clanId;
  private UUID userId;
  private JoinRequestStatus status;
  private Instant createdAt;
  private Instant resolvedAt;

  public static JoinRequest create(UUID clanId, UUID userId) {
    JoinRequest req = new JoinRequest();
    req.id = UUID.randomUUID();
    req.clanId = clanId;
    req.userId = userId;
    req.status = JoinRequestStatus.PENDING;
    req.createdAt = Instant.now();
    req.resolvedAt = null;
    return req;
  }

  public void approve() {
    if (status != JoinRequestStatus.PENDING) {
      throw new IllegalStateException("Cannot approve request with status: " + status);
    }
    status = JoinRequestStatus.APPROVED;
    resolvedAt = Instant.now();
  }

  public void reject() {
    if (status != JoinRequestStatus.PENDING) {
      throw new IllegalStateException("Cannot reject request with status: " + status);
    }
    status = JoinRequestStatus.REJECTED;
    resolvedAt = Instant.now();
  }
}
