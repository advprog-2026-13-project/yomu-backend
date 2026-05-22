package id.ac.ui.cs.advprog.yomu.backend.forum.infrastructure;

import id.ac.ui.cs.advprog.yomu.backend.forum.application.port.out.ForumEventPublisherPort;
import id.ac.ui.cs.advprog.yomu.backend.forum.events.CommentCreatedEvent;
import id.ac.ui.cs.advprog.yomu.backend.forum.events.CommentDeletedEvent;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ForumEventPublisherAdapter implements ForumEventPublisherPort {

  private final ApplicationEventPublisher eventPublisher;

  public ForumEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void publishCommentCreated(UUID commentId, UUID authorId, UUID readingId) {
    eventPublisher.publishEvent(new CommentCreatedEvent(commentId, authorId, readingId));
  }

  @Override
  public void publishCommentDeleted(UUID commentId, UUID requesterId, boolean isAdmin) {
    eventPublisher.publishEvent(new CommentDeletedEvent(commentId, requesterId, isAdmin));
  }
}
