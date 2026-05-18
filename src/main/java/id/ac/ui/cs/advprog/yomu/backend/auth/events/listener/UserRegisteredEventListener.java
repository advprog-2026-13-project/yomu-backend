package id.ac.ui.cs.advprog.yomu.backend.auth.events.listener;

import id.ac.ui.cs.advprog.yomu.backend.auth.events.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserRegisteredEventListener {

  private static final Logger log = LoggerFactory.getLogger(UserRegisteredEventListener.class);

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleUserRegistered(UserRegisteredEvent event) {
    log.info(
        "Post-registration routine for user {} (id={}, displayName={})",
        event.getUsername(),
        event.getUserId(),
        event.getDisplayName());
  }
}
