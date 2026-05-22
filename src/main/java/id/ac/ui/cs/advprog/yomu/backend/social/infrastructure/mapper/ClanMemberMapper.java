package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanMemberJpaEntity;

public final class ClanMemberMapper {

  private ClanMemberMapper() {}

  public static ClanMember toDomain(ClanMemberJpaEntity entity) {
    ClanMember member = new ClanMember();
    member.setId(entity.getId());
    member.setClanId(entity.getClanId());
    member.setUserId(entity.getUserId());
    member.setRole(entity.getRole());
    member.setJoinedAt(entity.getJoinedAt());
    return member;
  }

  public static ClanMemberJpaEntity toEntity(ClanMember member) {
    ClanMemberJpaEntity entity = new ClanMemberJpaEntity();
    entity.setId(member.getId());
    entity.setClanId(member.getClanId());
    entity.setUserId(member.getUserId());
    entity.setRole(member.getRole());
    entity.setJoinedAt(member.getJoinedAt());
    return entity;
  }
}
