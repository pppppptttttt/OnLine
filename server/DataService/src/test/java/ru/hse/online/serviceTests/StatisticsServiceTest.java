package ru.hse.online.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.online.mapper.UserStatisticsMapper;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.repository.UserStatisticsRepository;
import ru.hse.online.service.core.StatisticsCoreService;
import ru.hse.online.storage.UserStatisticsData;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private UserStatisticsRepository statisticsRepository;

    @InjectMocks
    private StatisticsCoreService statisticsCoreService;

    @Test
    void getStatisticsForPeriodReturnsListOfStatistics() {
        UUID userId = UUID.randomUUID();
        String name = "steps";

        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 2, 1);

        UserStatistics stat1 = UserStatistics.builder()
                .userId(userId)
                .name(name)
                .timestamp(start)
                .value(100.0)
                .build();

        UserStatistics stat2 = UserStatistics.builder()
                .userId(userId)
                .name(name)
                .timestamp(end)
                .value(150.0)
                .build();

        UserStatisticsData statData1 = UserStatisticsMapper.toData(stat1);
        UserStatisticsData statData2 = UserStatisticsMapper.toData(stat2);

        List<UserStatisticsData> expectedStats = Arrays.asList(statData1, statData2);

        when(statisticsRepository.findByUserIdAndNameAndTimestampBetween(userId, name, start, end))
                .thenReturn(expectedStats);

        List<UserStatistics> actualStats = statisticsCoreService.getStatisticsForPeriod(userId, name, start, end);

        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(UserStatisticsMapper.toModel(expectedStats.get(0)), actualStats.get(0));
        assertEquals(UserStatisticsMapper.toModel(expectedStats.get(1)), actualStats.get(1));

        verify(statisticsRepository).findByUserIdAndNameAndTimestampBetween(userId, name, start, end);
    }

    @Test
    void saveStatisticsCallsSaveAllMethod() {
        UUID userId = UUID.randomUUID();
        String name = "steps";

        LocalDate date = LocalDate.of(2025, 1, 2);

        UserStatistics stat1 = UserStatistics.builder()
                .userId(userId)
                .name(name)
                .timestamp(date)
                .value(100.0)
                .build();

        UserStatistics stat2 = UserStatistics.builder()
                .userId(userId)
                .name(name)
                .timestamp(date)
                .value(150.0)
                .build();

        List<UserStatistics> userStats = Arrays.asList(stat1, stat2);

        statisticsCoreService.saveStatistics(userStats);

        verify(statisticsRepository).saveAll(Arrays.asList(UserStatisticsMapper.toData(stat1), UserStatisticsMapper.toData(stat2)));
    }
}
