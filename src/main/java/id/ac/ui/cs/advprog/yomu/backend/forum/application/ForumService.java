package id.ac.ui.cs.advprog.yomu.backend.forum.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.CommentView;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.dto.UserSummary;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumBadRequestException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumForbiddenException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.exception.ForumNotFoundException;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.in.ForumUseCase;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.CommentRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ForumEventPublisherPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ReactionRepositoryPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.UserPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Comment;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.Reaction;
import id.ac.ui.cs.advprog.yomu.backend.forum.domain.ReactionType;

@Service
public class ForumService implements ForumUseCase {
	private static final int MAX_CONTENT_LENGTH = 2000;

	private final CommentRepositoryPort commentRepository;
	private final ReactionRepositoryPort reactionRepository;
	private final UserPort userRepository;
	private final ForumEventPublisherPort eventPublisher;

	public ForumService(
			CommentRepositoryPort commentRepository,
			ReactionRepositoryPort reactionRepository,
			UserPort userRepository,
			ForumEventPublisherPort eventPublisher) {
		this.commentRepository = commentRepository;
		this.reactionRepository = reactionRepository;
		this.userRepository = userRepository;
		this.eventPublisher = eventPublisher;
	}

	@Transactional(readOnly = true)
	@Override
	public List<CommentView> getComments(UUID readingId) {
		List<Comment> comments = commentRepository.findByReadingIdOrderByCreatedAtAsc(readingId);
		if (comments.isEmpty()) {
			return List.of();
		}

		Map<UUID, UserSummary> authors = resolveAuthors(comments);
		Map<UUID, List<Comment>> childrenByParentId = groupChildren(comments);

		Set<UUID> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toSet());
		Map<UUID, Map<ReactionType, Long>> reactionCounts = countReactions(commentIds);

		return comments.stream()
				.filter(c -> c.getParentId() == null)
				.sorted(Comparator.comparing(Comment::getCreatedAt))
				.map(c -> toView(c, childrenByParentId, reactionCounts, authors))
				.toList();
	}

	@Transactional
	@Override
	public CommentView postComment(UUID readingId, UUID userId, String content) {
		validateContent(content);

		Comment comment = new Comment();
		comment.setReadingId(readingId);
		comment.setAuthorId(userId);
		comment.setParentId(null);
		comment.setContent(content);
		comment.setDeleted(false);
		comment.setCreatedAt(Instant.now());

		Comment saved = commentRepository.save(comment);
		eventPublisher.publishCommentCreated(saved.getId(), saved.getAuthorId(), saved.getReadingId());
		return toSingleView(saved);
	}

	@Transactional
	@Override
	public CommentView replyToComment(UUID parentCommentId, UUID userId, String content) {
		validateContent(content);

		Comment parent =
				commentRepository
						.findById(parentCommentId)
						.orElseThrow(
								() -> new ForumNotFoundException("Parent comment not found"));

		if (parent.isDeleted()) {
			throw new ForumBadRequestException("Cannot reply to a deleted comment");
		}

		Comment reply = new Comment();
		reply.setReadingId(parent.getReadingId());
		reply.setAuthorId(userId);
		reply.setParentId(parent.getId());
		reply.setContent(content);
		reply.setDeleted(false);
		reply.setCreatedAt(Instant.now());

		Comment saved = commentRepository.save(reply);
		eventPublisher.publishCommentCreated(saved.getId(), saved.getAuthorId(), saved.getReadingId());
		return toSingleView(saved);
	}

	@Transactional
	@Override
	public void editComment(UUID commentId, UUID requesterId, String newContent) {
		validateContent(newContent);

		Comment comment =
				commentRepository
						.findById(commentId)
						.orElseThrow(() -> new ForumNotFoundException("Comment not found"));

		if (comment.isDeleted()) {
			throw new ForumBadRequestException("Cannot edit a deleted comment");
		}

		if (!Objects.equals(comment.getAuthorId(), requesterId)) {
			throw new ForumForbiddenException("Only the author can edit this comment");
		}

		comment.setContent(newContent);
		comment.setEditedAt(Instant.now());
		commentRepository.save(comment);
	}

	@Transactional
	@Override
	public void deleteComment(UUID commentId, UUID requesterId, boolean isAdmin) {
		Comment comment =
				commentRepository
						.findById(commentId)
						.orElseThrow(() -> new ForumNotFoundException("Comment not found"));

		if (!isAdmin && !Objects.equals(comment.getAuthorId(), requesterId)) {
			throw new ForumForbiddenException("You do not have permission to delete this comment");
		}

		if (comment.isDeleted()) {
			return;
		}

		comment.setDeleted(true);
		commentRepository.save(comment);
		eventPublisher.publishCommentDeleted(commentId, requesterId, isAdmin);
	}

	@Transactional
	@Override
	public void toggleReaction(UUID commentId, UUID userId, ReactionType type) {
		Comment comment =
				commentRepository
						.findById(commentId)
						.orElseThrow(() -> new ForumNotFoundException("Comment not found"));

		if (comment.isDeleted()) {
			throw new ForumBadRequestException("Cannot react to a deleted comment");
		}

		boolean exists = reactionRepository.existsByCommentIdAndUserIdAndType(commentId, userId, type);
		if (exists) {
			reactionRepository.deleteByCommentIdAndUserIdAndType(commentId, userId, type);
			return;
		}

		if (type.isVote()) {
			ReactionType opposite = (type == ReactionType.UPVOTE) ? ReactionType.DOWNVOTE : ReactionType.UPVOTE;
			reactionRepository.deleteByCommentIdAndUserIdAndTypeIn(commentId, userId, List.of(opposite));
		}

		Reaction reaction = new Reaction();
		reaction.setCommentId(commentId);
		reaction.setUserId(userId);
		reaction.setType(type);
		reaction.setCreatedAt(Instant.now());
		reactionRepository.save(reaction);
	}

	private void validateContent(String content) {
		if (content == null || content.isBlank()) {
			throw new ForumBadRequestException("Content must not be empty");
		}
		if (content.length() > MAX_CONTENT_LENGTH) {
			throw new ForumBadRequestException("Content too long (max " + MAX_CONTENT_LENGTH + ")");
		}
	}

	private CommentView toSingleView(Comment comment) {
		UserSummary author = resolveAuthor(comment.getAuthorId());
		Map<ReactionType, Long> reactionCounts = Map.of();
		return new CommentView(
				comment.getId(),
				comment.getReadingId(),
				author,
				comment.getParentId(),
				comment.isDeleted() ? null : comment.getContent(),
				comment.isDeleted(),
				comment.getCreatedAt(),
				comment.getEditedAt(),
				reactionCounts,
				List.of());
	}

	private CommentView toView(
			Comment comment,
			Map<UUID, List<Comment>> childrenByParentId,
			Map<UUID, Map<ReactionType, Long>> reactionCounts,
			Map<UUID, UserSummary> authors) {

		List<CommentView> replies =
				childrenByParentId.getOrDefault(comment.getId(), List.of()).stream()
						.sorted(Comparator.comparing(Comment::getCreatedAt))
						.map(c -> toView(c, childrenByParentId, reactionCounts, authors))
						.toList();

		Map<ReactionType, Long> counts = reactionCounts.getOrDefault(comment.getId(), Map.of());
		UserSummary author = authors.getOrDefault(comment.getAuthorId(), new UserSummary(comment.getAuthorId(), null));

		return new CommentView(
				comment.getId(),
				comment.getReadingId(),
				author,
				comment.getParentId(),
				comment.isDeleted() ? null : comment.getContent(),
				comment.isDeleted(),
				comment.getCreatedAt(),
				comment.getEditedAt(),
				counts,
				replies);
	}

	private Map<UUID, UserSummary> resolveAuthors(List<Comment> comments) {
		Set<UUID> authorIds = comments.stream().map(Comment::getAuthorId).collect(Collectors.toSet());
		if (authorIds.isEmpty()) {
			return Map.of();
		}

		return userRepository.findAllById(authorIds).stream()
				.collect(
						Collectors.toMap(
								u -> u.getId(), u -> new UserSummary(u.getId(), u.getDisplayName())));
	}

	private UserSummary resolveAuthor(UUID authorId) {
		return userRepository
				.findById(authorId)
				.map(u -> new UserSummary(u.getId(), u.getDisplayName()))
				.orElseGet(() -> new UserSummary(authorId, null));
	}

	private Map<UUID, List<Comment>> groupChildren(List<Comment> comments) {
		Map<UUID, List<Comment>> childrenByParentId = new HashMap<>();
		for (Comment comment : comments) {
			UUID parentId = comment.getParentId();
			if (parentId == null) {
				continue;
			}
			childrenByParentId.computeIfAbsent(parentId, k -> new ArrayList<>()).add(comment);
		}
		return childrenByParentId;
	}

	private Map<UUID, Map<ReactionType, Long>> countReactions(Set<UUID> commentIds) {
		if (commentIds.isEmpty()) {
			return Map.of();
		}

		List<Reaction> reactions = reactionRepository.findByCommentIdIn(commentIds);

		return reactions.stream()
				.collect(
						Collectors.groupingBy(
								Reaction::getCommentId,
								Collectors.groupingBy(Reaction::getType, Collectors.counting())));
	}
}
