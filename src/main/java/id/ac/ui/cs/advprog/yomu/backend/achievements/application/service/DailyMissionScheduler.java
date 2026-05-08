package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserDailyMissionProgressRepository;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyMissionScheduler {

  private static final Logger logger = LoggerFactory.getLogger(DailyMissionScheduler.class);
  private final IUserDailyMissionProgressRepository userDailyMissionProgressRepository;

  public DailyMissionScheduler(
      IUserDailyMissionProgressRepository userDailyMissionProgressRepository) {
    this.userDailyMissionProgressRepository = userDailyMissionProgressRepository;
  }

  /**
   * Resets/cleans up daily mission progress. Runs every day at 00:00:00. It deletes the daily
   * missions progress from the previous day to keep the database clean.
   */
  @Scheduled(cron = "0 0 0 * * ?")
  public void resetDailyMissions() {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    logger.info("Running daily mission cleanup for date: {}", yesterday);

    userDailyMissionProgressRepository.deleteByDate(yesterday);

    logger.info("Successfully cleaned up daily missions for date: {}", yesterday);
  }
}
