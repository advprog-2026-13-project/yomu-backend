package id.ac.ui.cs.advprog.yomu.backend.forum.api;

import id.ac.ui.cs.advprog.yomu.backend.auth.infrastructure.security.SecurityUser;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.EditCommentRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.PostCommentRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.ReactRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.api.dto.ReplyRequest;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in.ForumUseCase;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

	private final ForumUseCase forumUseCase;

	public ForumController(ForumUseCase forumUseCase) {
		this.forumUseCase = forumUseCase;
	}

	// GET /api/forums/{readingId}/comments
	@GetMapping("/{readingId}/comments")
	public ResponseEntity<List<CommentView>> getComments(
			@PathVariable("readingId") UUID readingId) {
		return ResponseEntity.ok(forumUseCase.getComments(readingId));
	}

	// POST /api/forums/{readingId}/comments
	@PostMapping("/{readingId}/comments")
	public ResponseEntity<CommentView> postComment(
			@PathVariable("readingId") UUID readingId,
			@Valid @RequestBody PostCommentRequest request,
			@AuthenticationPrincipal SecurityUser principal) {

		UUID userId = principal.getUser().getId();
		CommentView created = forumUseCase.postComment(readingId, userId, request.getContent());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	// POST /api/forums/comments/{id}/replies
	@PostMapping("/comments/{id}/replies")
	public ResponseEntity<CommentView> replyToComment(
			@PathVariable("id") UUID id,
			@Valid @RequestBody ReplyRequest request,
			@AuthenticationPrincipal SecurityUser principal) {

		UUID userId = principal.getUser().getId();
		CommentView created = forumUseCase.replyToComment(id, userId, request.getContent());
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	// PUT /api/forums/comments/{id}
	@PutMapping("/comments/{id}")
	public ResponseEntity<Void> editComment(
			@PathVariable("id") UUID id,
			@Valid @RequestBody EditCommentRequest request,
			@AuthenticationPrincipal SecurityUser principal) {

		UUID userId = principal.getUser().getId();
		forumUseCase.editComment(id, userId, request.getNewContent());
		return ResponseEntity.noContent().build();
	}

	// DELETE /api/forums/comments/{id}
	@DeleteMapping("/comments/{id}")
	public ResponseEntity<Void> deleteComment(
			@PathVariable("id") UUID id, @AuthenticationPrincipal SecurityUser principal) {
		UUID userId = principal.getUser().getId();
		forumUseCase.deleteComment(id, userId, false);
		return ResponseEntity.noContent().build();
	}

	// POST /api/forums/comments/{id}/reactions
	@PostMapping("/comments/{id}/reactions")
	public ResponseEntity<Void> react(
			@PathVariable("id") UUID id,
			@Valid @RequestBody ReactRequest request,
			@AuthenticationPrincipal SecurityUser principal) {

		UUID userId = principal.getUser().getId();
		forumUseCase.toggleReaction(id, userId, request.getType());
		return ResponseEntity.noContent().build();
	}
}
