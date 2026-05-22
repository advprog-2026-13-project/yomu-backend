package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper.ClanMemberMapper;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanMemberJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataClanMemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ClanMemberRepositoryAdapter implements ClanMemberRepositoryPort {

  private final SpringDataClanMemberRepository jpa;

  public ClanMemberRepositoryAdapter(SpringDataClanMemberRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Optional<ClanMember> findByUserId(UUID userId) {
    return jpa.findByUserId(userId).map(ClanMemberMapper::toDomain);
  }

  @Override
  public List<ClanMember> findByClanId(UUID clanId) {
    return jpa.findByClanId(clanId).stream().map(ClanMemberMapper::toDomain).toList();
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    return jpa.existsByUserId(userId);
  }

  @Override
  public long countByClanId(UUID clanId) {
    return jpa.countByClanId(clanId);
  }

  @Override
  public ClanMember save(ClanMember member) {
    ClanMemberJpaEntity entity = ClanMemberMapper.toEntity(member);
    ClanMemberJpaEntity saved = jpa.save(entity);
    return ClanMemberMapper.toDomain(saved);
  }

  @Override
  public void delete(ClanMember member) {
    jpa.deleteById(member.getId());
  }

  @Override
  public void deleteAll(Iterable<ClanMember> members) {
    List<ClanMemberJpaEntity> entities = new ArrayList<>();
    members.forEach(m -> entities.add(ClanMemberMapper.toEntity(m)));
    jpa.deleteAll(entities);
  }
}
