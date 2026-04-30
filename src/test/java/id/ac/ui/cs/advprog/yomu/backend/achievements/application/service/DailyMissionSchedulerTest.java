package id.ac.ui.cs.advprog.yomu.backend.achievements.application.service;

import id.ac.ui.cs.advprog.yomu.backend.achievements.application.port.out.IUserDailyMissionProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

class DailyMissionSchedulerTest {

    @Mock
    private IUserDailyMissionProgressRepository userDailyMissionProgressRepository;

    @InjectMocks
    private DailyMissionScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testResetDailyMissions() {
        scheduler.resetDailyMissions();
        
        // It should delete missions from yesterday
        LocalDate yesterday = LocalDate.now().minusDays(1);
        verify(userDailyMissionProgressRepository, times(1)).deleteByDate(yesterday);
    }
}
