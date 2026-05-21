package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper;

import id.ac.ui.cs.advprog.yomu.backend.social.domain.JoinRequest;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.JoinRequestJpaEntity;

public final class JoinRequestMapper {

  private JoinRequestMapper() {}

  public static JoinRequest toDomain(JoinRequestJpaEntity entity) {
    JoinRequest req = new JoinRequest();
    req.setId(entity.getId());
    req.setClanId(entity.getClanId());
    req.setUserId(entity.getUserId());
    req.setStatus(entity.getStatus());
    req.setCreatedAt(entity.getCreatedAt());
    req.setResolvedAt(entity.getResolvedAt());
    return req;
  }

  public static JoinRequestJpaEntity toEntity(JoinRequest req) {
    JoinRequestJpaEntity entity = new JoinRequestJpaEntity();
    entity.setId(req.getId());
    entity.setClanId(req.getClanId());
    entity.setUserId(req.getUserId());
    entity.setStatus(req.getStatus());
    entity.setCreatedAt(req.getCreatedAt());
    entity.setResolvedAt(req.getResolvedAt());
    return entity;
  }
}
