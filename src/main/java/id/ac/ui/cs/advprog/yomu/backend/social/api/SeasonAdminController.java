package id.ac.ui.cs.advprog.yomu.backend.social.api;

import id.ac.ui.cs.advprog.yomu.backend.social.application.SeasonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/social")
@PreAuthorize("hasRole('ADMIN')")
public class SeasonAdminController {

  private final SeasonService seasonService;

  public SeasonAdminController(SeasonService seasonService) {
    this.seasonService = seasonService;
  }

  @PostMapping("/seasons/end")
  public ResponseEntity<Void> endSeason() {
    seasonService.endSeason();
    return ResponseEntity.ok().build();
  }
}
