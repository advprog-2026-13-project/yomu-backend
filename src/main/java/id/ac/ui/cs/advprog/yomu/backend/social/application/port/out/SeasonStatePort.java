package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.time.LocalDateTime;

public interface SeasonStatePort {

  LocalDateTime getCurrentSeasonStart();

  void startNewSeason(LocalDateTime startedAt);
}
