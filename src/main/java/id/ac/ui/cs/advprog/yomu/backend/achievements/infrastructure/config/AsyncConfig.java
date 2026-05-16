package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for asynchronous event processing in the achievement module.
 *
 * <p>Achievement event listeners run asynchronously so they don't block the publisher's thread
 * (e.g., the reading or quiz module). A dedicated thread pool isolates achievement processing from
 * the rest of the application.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

  /**
   * Dedicated thread pool for achievement event processing.
   *
   * <ul>
   *   <li><b>corePoolSize=2</b> — handles normal load
   *   <li><b>maxPoolSize=5</b> — scales for bursts
   *   <li><b>queueCapacity=100</b> — buffers up to 100 events before rejecting
   * </ul>
   */
  @Bean(name = "achievementTaskExecutor")
  public TaskExecutor achievementTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("achievement-");
    executor.initialize();
    return executor;
  }
}
