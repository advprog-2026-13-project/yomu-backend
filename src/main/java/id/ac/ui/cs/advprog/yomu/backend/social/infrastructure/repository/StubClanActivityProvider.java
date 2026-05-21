package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import java.util.UUID;
import org.springframework.stereotype.Component;

// TODO: STUB — ganti dengan adapter riil saat achievements/reading publish event
// (lihat integration-notes.md: butuh DailyMissionCompletionRate dari achievements
// dan averageAccuracy dari reading)
@Component
public class StubClanActivityProvider implements ClanActivityProvider {

  @Override
  public ClanActivitySnapshot getActivity(UUID clanId) {
    // completionRate=0.0, accuracy=1.0 → tidak ada buff/debuff aktif
    return new ClanActivitySnapshot(0.0, 1.0);
  }
}
