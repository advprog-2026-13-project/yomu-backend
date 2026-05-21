package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class JoinRequestTest {

  private static final UUID CLAN_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();

  @Test
  void create_newRequest_isPendingWithNullResolvedAt() {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);

    assertEquals(CLAN_ID, req.getClanId());
    assertEquals(USER_ID, req.getUserId());
    assertEquals(JoinRequestStatus.PENDING, req.getStatus());
    assertNotNull(req.getCreatedAt());
    assertNull(req.getResolvedAt());
  }

  @Test
  void approve_fromPending_setsApprovedAndResolvedAt() {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);

    req.approve();

    assertEquals(JoinRequestStatus.APPROVED, req.getStatus());
    assertNotNull(req.getResolvedAt());
  }

  @Test
  void reject_fromPending_setsRejectedAndResolvedAt() {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);

    req.reject();

    assertEquals(JoinRequestStatus.REJECTED, req.getStatus());
    assertNotNull(req.getResolvedAt());
  }

  @Test
  void approve_fromApproved_throwsIllegalStateException() {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);
    req.approve();

    assertThrows(IllegalStateException.class, req::approve);
  }

  @Test
  void reject_fromRejected_throwsIllegalStateException() {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);
    req.reject();

    assertThrows(IllegalStateException.class, req::reject);
  }

  @Test
  void approve_fromRejected_throwsIllegalStateException() {
    JoinRequest req = JoinRequest.create(CLAN_ID, USER_ID);
    req.reject();

    assertThrows(IllegalStateException.class, req::approve);
  }
}
