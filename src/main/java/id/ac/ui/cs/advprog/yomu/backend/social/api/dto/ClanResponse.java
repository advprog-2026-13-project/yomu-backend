package id.ac.ui.cs.advprog.yomu.backend.social.api.dto;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanModifierStatus;
import java.util.UUID;

public class ClanResponse {

  private final UUID id;
  private final String name;
  private final Tier tier;
  private final long score;
  private final UUID leaderId;
  private final long memberCount;
  private final ClanModifierStatus modifiers;

  public ClanResponse(Clan clan, long memberCount) {
    this(clan, memberCount, null);
  }

  public ClanResponse(Clan clan, long memberCount, ClanModifierStatus modifiers) {
    this.id = clan.getId();
    this.name = clan.getName();
    this.tier = clan.getTier();
    this.score = clan.getScore();
    this.leaderId = clan.getLeaderId();
    this.memberCount = memberCount;
    this.modifiers = modifiers;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Tier getTier() {
    return tier;
  }

  public long getScore() {
    return score;
  }

  public UUID getLeaderId() {
    return leaderId;
  }

  public long getMemberCount() {
    return memberCount;
  }

  public ClanModifierStatus getModifiers() {
    return modifiers;
  }
}
