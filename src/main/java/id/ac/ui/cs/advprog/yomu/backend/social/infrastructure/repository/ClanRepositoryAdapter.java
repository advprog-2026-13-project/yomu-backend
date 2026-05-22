package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Clan;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.Tier;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.mapper.ClanMapper;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.ClanJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataClanRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ClanRepositoryAdapter implements ClanRepositoryPort {

  private final SpringDataClanRepository jpa;

  public ClanRepositoryAdapter(SpringDataClanRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public boolean existsByName(String name) {
    return jpa.existsByName(name);
  }

  @Override
  public Optional<Clan> findById(UUID id) {
    return jpa.findById(id).map(ClanMapper::toDomain);
  }

  @Override
  public Clan save(Clan clan) {
    ClanJpaEntity entity = ClanMapper.toEntity(clan);
    ClanJpaEntity saved = jpa.save(entity);
    return ClanMapper.toDomain(saved);
  }

  @Override
  public void deleteById(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public List<Clan> findByTierOrderByScoreDesc(Tier tier) {
    return jpa.findByTierOrderByScoreDesc(tier).stream().map(ClanMapper::toDomain).toList();
  }

  @Override
  public List<Clan> findAll() {
    return jpa.findAll().stream().map(ClanMapper::toDomain).toList();
  }
}
