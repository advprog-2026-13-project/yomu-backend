package id.ac.ui.cs.advprog.yomu.backend.forum.application.dto;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CommentView(
    UUID id,
    UUID readingId,
    UUID authorId,
    String authorName,
    UUID parentId,
    String content,
    boolean deleted,
    Instant createdAt,
    Instant editedAt,
    Map<ReactionType, Long> reactionCounts,
    List<CommentView> replies) {}
