package id.ac.ui.cs.advprog.yomu.backend.achievements;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "http://localhost:3000")
public class AchievementDummyController {

  private final AchievementDummyLogRepository repository;

  public AchievementDummyController(AchievementDummyLogRepository repository) {
    this.repository = repository;
  }

  @PostMapping("/dummy-hit")
  public Map<String, Object> dummyHit() {
    AchievementDummyLog log = new AchievementDummyLog();
    log.setEventName("DUMMY_ACHIEVEMENT_HIT");
    repository.save(log);

    long total = repository.count();

    return Map.of("saved", true, "event", "DUMMY_ACHIEVEMENT_HIT", "totalHits", total);
  }

  @GetMapping("/dummy-hit")
  public Map<String, Object> getTotal() {
    long total = repository.count();
    return Map.of("totalHits", total);
  }
}
