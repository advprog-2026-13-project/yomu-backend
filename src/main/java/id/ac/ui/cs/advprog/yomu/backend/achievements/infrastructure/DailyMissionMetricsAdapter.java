package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementQueryService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.DailyMissionMetricsPort;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DailyMissionMetricsAdapter implements DailyMissionMetricsPort {

  private final AchievementQueryService achievementQueryService;

  public DailyMissionMetricsAdapter(AchievementQueryService achievementQueryService) {
    this.achievementQueryService = achievementQueryService;
  }

  @Override
  public long countCompletedByUsersOnDate(List<UUID> userIds, LocalDate date) {
    return achievementQueryService.countMembersCompletedDailyMissionOn(userIds, date);
  }
}
