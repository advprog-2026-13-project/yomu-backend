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
public class Clan {

  private UUID id;
  private String name;
  private Tier tier;
  private long score;
  private UUID leaderId;
  private Instant createdAt;

  public static Clan createNew(String name, UUID leaderId) {
    Clan clan = new Clan();
    clan.setName(name);
    clan.setLeaderId(leaderId);
    clan.setTier(Tier.BRONZE);
    clan.setScore(0L);
    return clan;
  }
}
