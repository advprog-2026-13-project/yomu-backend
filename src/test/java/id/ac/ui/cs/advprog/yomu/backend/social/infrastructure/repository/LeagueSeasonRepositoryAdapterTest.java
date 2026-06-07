package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.persistence.LeagueSeasonJpaEntity;
import id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository.jpa.SpringDataLeagueSeasonRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LeagueSeasonRepositoryAdapterTest {

  @Mock private SpringDataLeagueSeasonRepository jpa;
  @InjectMocks private LeagueSeasonRepositoryAdapter adapter;

  @Test
  void getCurrentSeasonStart_returnsStoredValue_whenEntityExists() {
    LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
    LeagueSeasonJpaEntity entity = new LeagueSeasonJpaEntity(1, start);
    when(jpa.findById(1)).thenReturn(Optional.of(entity));

    assertEquals(start, adapter.getCurrentSeasonStart());
  }

  @Test
  void getCurrentSeasonStart_returnsLocalDateTimeMin_whenNoEntityExists() {
    when(jpa.findById(1)).thenReturn(Optional.empty());

    assertEquals(LocalDateTime.MIN, adapter.getCurrentSeasonStart());
  }

  @Test
  void startNewSeason_updatesExistingEntity() {
    LocalDateTime newStart = LocalDateTime.of(2026, 6, 1, 0, 0);
    LeagueSeasonJpaEntity existing = new LeagueSeasonJpaEntity(1, LocalDateTime.MIN);
    when(jpa.findById(1)).thenReturn(Optional.of(existing));

    adapter.startNewSeason(newStart);

    ArgumentCaptor<LeagueSeasonJpaEntity> captor =
        ArgumentCaptor.forClass(LeagueSeasonJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertEquals(newStart, captor.getValue().getStartedAt());
    assertEquals(1, captor.getValue().getId());
  }

  @Test
  void startNewSeason_createsNewEntityWhenNoneExists() {
    LocalDateTime newStart = LocalDateTime.of(2026, 6, 1, 0, 0);
    when(jpa.findById(1)).thenReturn(Optional.empty());

    adapter.startNewSeason(newStart);

    ArgumentCaptor<LeagueSeasonJpaEntity> captor =
        ArgumentCaptor.forClass(LeagueSeasonJpaEntity.class);
    verify(jpa).save(captor.capture());
    assertEquals(newStart, captor.getValue().getStartedAt());
    assertEquals(1, captor.getValue().getId());
  }
}
