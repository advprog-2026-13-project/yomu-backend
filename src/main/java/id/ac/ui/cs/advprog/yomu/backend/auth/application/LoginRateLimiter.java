package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoginRateLimiter {

  private static final Logger log = LoggerFactory.getLogger(LoginRateLimiter.class);

  private static final int MAX_ATTEMPTS = 5;

  private final Map<String, AtomicInteger> failedAttempts = new ConcurrentHashMap<>();

  public boolean isBlocked(String username) {
    AtomicInteger attempts = failedAttempts.get(username);
    return attempts != null && attempts.get() >= MAX_ATTEMPTS;
  }

  public void recordFailure(String username) {
    AtomicInteger attempts = failedAttempts.computeIfAbsent(username, k -> new AtomicInteger(0));
    int current = attempts.incrementAndGet();
    log.warn(
        "Authentication failure for user '{}' (attempt {}/{})", username, current, MAX_ATTEMPTS);
  }

  public void reset(String username) {
    failedAttempts.remove(username);
  }
}
