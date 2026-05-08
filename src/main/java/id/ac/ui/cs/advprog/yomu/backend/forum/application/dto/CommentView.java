package id.ac.ui.cs.advprog.yomu.backend.forum.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;

public record CommentView(
    UUID id,
    UUID readingId,
    UserSummary author,
    UUID parentId,
    String content,
    boolean deleted,
    Instant createdAt,
    Instant editedAt,
    Map<ReactionType, Long> reactionCounts,
    List<CommentView> replies) {}
