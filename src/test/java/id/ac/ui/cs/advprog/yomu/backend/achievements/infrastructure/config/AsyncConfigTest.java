package id.ac.ui.cs.advprog.yomu.backend.achievements.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

class AsyncConfigTest {

  private final AsyncConfig asyncConfig = new AsyncConfig();

  @Test
  void achievementTaskExecutorShouldBeCreated() {
    TaskExecutor executor = asyncConfig.achievementTaskExecutor();
    assertNotNull(executor);
    assertInstanceOf(ThreadPoolTaskExecutor.class, executor);
  }

  @Test
  void achievementTaskExecutorShouldHaveCorrectCorePoolSize() {
    ThreadPoolTaskExecutor executor =
        (ThreadPoolTaskExecutor) asyncConfig.achievementTaskExecutor();
    assertEquals(2, executor.getCorePoolSize());
  }

  @Test
  void achievementTaskExecutorShouldHaveCorrectMaxPoolSize() {
    ThreadPoolTaskExecutor executor =
        (ThreadPoolTaskExecutor) asyncConfig.achievementTaskExecutor();
    assertEquals(5, executor.getMaxPoolSize());
  }

  @Test
  void achievementTaskExecutorShouldHaveCorrectThreadPrefix() {
    ThreadPoolTaskExecutor executor =
        (ThreadPoolTaskExecutor) asyncConfig.achievementTaskExecutor();
    assertEquals("achievement-", executor.getThreadNamePrefix());
  }
}
