package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanJpaEntity;

public final class ClanMapper {

  private ClanMapper() {}

  public static Clan toDomain(ClanJpaEntity entity) {
    Clan clan = new Clan();
    clan.setId(entity.getId());
    clan.setName(entity.getName());
    clan.setTier(entity.getTier());
    clan.setScore(entity.getScore());
    clan.setLeaderId(entity.getLeaderId());
    clan.setCreatedAt(entity.getCreatedAt());
    return clan;
  }

  public static ClanJpaEntity toEntity(Clan clan) {
    ClanJpaEntity entity = new ClanJpaEntity();
    entity.setId(clan.getId());
    entity.setName(clan.getName());
    entity.setTier(clan.getTier());
    entity.setScore(clan.getScore());
    entity.setLeaderId(clan.getLeaderId());
    entity.setCreatedAt(clan.getCreatedAt());
    return entity;
  }
}
