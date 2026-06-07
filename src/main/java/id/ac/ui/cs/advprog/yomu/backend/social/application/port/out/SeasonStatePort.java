package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.time.LocalDateTime;

public interface SeasonStatePort {

  /** Waktu mulai season liga saat ini. */
  LocalDateTime getCurrentSeasonStart();

  /** Tandai season baru dimulai pada waktu tertentu (dipanggil saat end-season). */
  void startNewSeason(LocalDateTime startedAt);
}
