package id.ac.ui.cs.advprog.yomu.backend.auth.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoginRateLimiter {

  private static final Logger log = LoggerFactory.getLogger(LoginRateLimiter.class);

  private static final int MAX_ATTEMPTS = 5;
  private static final long EVICTION_MILLIS = 15L * 60 * 1000;

  private final Map<String, AttemptEntry> failedAttempts = new ConcurrentHashMap<>();

  private static class AttemptEntry {
    final AtomicInteger count = new AtomicInteger(0);
    volatile long lastUpdate = System.currentTimeMillis();
  }

  public boolean isBlocked(String username) {
    AttemptEntry entry = failedAttempts.get(username);
    if (entry == null) return false;
    if (isExpired(entry)) {
      failedAttempts.remove(username, entry);
      return false;
    }
    return entry.count.get() >= MAX_ATTEMPTS;
  }

  public void recordFailure(String username) {
    AttemptEntry entry = failedAttempts.computeIfAbsent(username, k -> new AttemptEntry());
    entry.lastUpdate = System.currentTimeMillis();
    int current = entry.count.incrementAndGet();
    log.warn(
        "Authentication failure for user hash={} (attempt {}/{})",
        Integer.toHexString(username.hashCode()),
        current,
        MAX_ATTEMPTS);
  }

  public void reset(String username) {
    failedAttempts.remove(username);
  }

  @Scheduled(fixedRate = 5 * 60 * 1000)
  public void evictExpired() {
    failedAttempts.entrySet().removeIf(e -> isExpired(e.getValue()));
  }

  private boolean isExpired(AttemptEntry entry) {
    return System.currentTimeMillis() - entry.lastUpdate > EVICTION_MILLIS;
  }
}
