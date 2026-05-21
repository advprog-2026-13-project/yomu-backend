package id.ac.ui.cs.advprog.yomu.backend.social.infrastructure.repository;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.service.AchievementQueryService;
import id.ac.ui.cs.advprog.yomu.backend.reading.application.QuizStatsQueryService;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanActivityProvider;
import id.ac.ui.cs.advprog.yomu.backend.social.application.port.out.ClanMemberRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.ClanMember;
import id.ac.ui.cs.advprog.yomu.backend.social.domain.modifier.ClanActivitySnapshot;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RealClanActivityProvider implements ClanActivityProvider {

  private final ClanMemberRepositoryPort clanMemberRepository;
  private final AchievementQueryService achievementQueryService;
  private final QuizStatsQueryService quizStatsQueryService;

  public RealClanActivityProvider(
      ClanMemberRepositoryPort clanMemberRepository,
      AchievementQueryService achievementQueryService,
      QuizStatsQueryService quizStatsQueryService) {
    this.clanMemberRepository = clanMemberRepository;
    this.achievementQueryService = achievementQueryService;
    this.quizStatsQueryService = quizStatsQueryService;
  }

  @Override
  public ClanActivitySnapshot getActivity(UUID clanId) {
    List<UUID> memberUserIds =
        clanMemberRepository.findByClanId(clanId).stream()
            .map(ClanMember::getUserId)
            .toList();

    if (memberUserIds.isEmpty()) {
      return new ClanActivitySnapshot(0.0, 1.0);
    }

    long completedCount =
        achievementQueryService.countMembersCompletedDailyMissionOn(
            memberUserIds, LocalDate.now());
    double completionRate = (double) completedCount / memberUserIds.size();
    double averageAccuracy = quizStatsQueryService.averageScore(memberUserIds);

    return new ClanActivitySnapshot(completionRate, averageAccuracy);
  }
}
