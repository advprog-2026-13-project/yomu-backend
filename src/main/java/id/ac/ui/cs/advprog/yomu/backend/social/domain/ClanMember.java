package id.ac.ui.cs.advprog.yomu.backend.social.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClanMember {

  private UUID id;
  private UUID clanId;
  private UUID userId;
  private ClanMemberRole role;
  private Instant joinedAt;

  public static ClanMember join(UUID clanId, UUID userId) {
    ClanMember member = new ClanMember();
    member.setClanId(clanId);
    member.setUserId(userId);
    member.setRole(ClanMemberRole.MEMBER);
    return member;
  }
}
