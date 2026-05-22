package id.ac.ui.cs.advprog.yomu.backend.social.application.port.out;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyMissionMetricsPort {

  long countCompletedByUsersOnDate(List<UUID> userIds, LocalDate date);
}
