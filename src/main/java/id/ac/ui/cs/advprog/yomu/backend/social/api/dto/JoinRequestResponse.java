package id.ac.ui.cs.advprog.yomu.backend.social.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequestStatus;
import java.time.Instant;
import java.util.UUID;

public class JoinRequestResponse {

  private final UUID id;
  private final UUID clanId;
  private final UUID userId;
  private final JoinRequestStatus status;
  private final Instant createdAt;
  private final Instant resolvedAt;

  public JoinRequestResponse(JoinRequest req) {
    this.id = req.getId();
    this.clanId = req.getClanId();
    this.userId = req.getUserId();
    this.status = req.getStatus();
    this.createdAt = req.getCreatedAt();
    this.resolvedAt = req.getResolvedAt();
  }

  public UUID getId() {
    return id;
  }

  public UUID getClanId() {
    return clanId;
  }

  public UUID getUserId() {
    return userId;
  }

  public JoinRequestStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getResolvedAt() {
    return resolvedAt;
  }
}
