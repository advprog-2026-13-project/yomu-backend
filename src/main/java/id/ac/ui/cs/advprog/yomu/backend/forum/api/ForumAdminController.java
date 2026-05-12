package id.ac.ui.cs.advprog.yomu.backend.forum.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in.ForumUseCase;

@RestController
@RequestMapping("/api/admin/forums")
@PreAuthorize("hasRole('ADMIN')")
public class ForumAdminController {

  private final ForumUseCase forumUseCase;

  public ForumAdminController(ForumUseCase forumUseCase) {
    this.forumUseCase = forumUseCase;
  }

  // DELETE /api/admin/forums/comments/{id}
  @DeleteMapping("/comments/{id}")
  public ResponseEntity<Void> moderateDelete(
      @PathVariable UUID id, @AuthenticationPrincipal SecurityUser principal) {
    UUID adminId = principal.getUser().getId();
    forumUseCase.deleteComment(id, adminId, true);
    return ResponseEntity.noContent().build();
  }
}
