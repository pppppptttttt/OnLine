package ru.hse.online.serviceTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import ru.hse.online.mapper.UserStatisticsMapper;
import ru.hse.online.model.User;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.repository.UserStatisticsRepository;
import ru.hse.online.service.FriendshipService;
import ru.hse.online.service.UserService;
import ru.hse.online.service.core.StatisticsCoreService;
import ru.hse.online.storage.UserStatisticsData;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private UserStatisticsRepository statisticsRepository;

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private UserService userService;

    @InjectMocks
    private StatisticsCoreService statisticsCoreService;

    @Test
    void getStatisticsForPeriodReturnsListOfStatistics() {
        UUID userId = UUID.randomUUID();
        String name = "STEPS";

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
        String name = "STEPS";

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

    @Test
    void getLeaderBoardReturnsSortedList() {
        UUID userId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        User user1 = User.builder().userId(UUID.randomUUID()).username("user1").build();
        User user2 = User.builder().userId(UUID.randomUUID()).username("user2").build();
        User mainUser = User.builder().userId(userId).username("mainUser").build();

        List<User> friendsList = new ArrayList<>(Arrays.asList(user1, user2));

        when(friendshipService.getFriendsList(userId))
                .thenReturn(friendsList);
        when(userService.getUserById(userId))
                .thenReturn(mainUser);

        when(statisticsRepository.findByUserIdAndNameAndTimestampBetween(
                user1.getUserId(), "STEPS", start, end))
                .thenReturn(Collections.singletonList(
                        createStatisticsData(user1.getUserId(), "STEPS", start, 500.0)
                ));

        when(statisticsRepository.findByUserIdAndNameAndTimestampBetween(
                user2.getUserId(), "STEPS", start, end))
                .thenReturn(Arrays.asList(
                        createStatisticsData(user2.getUserId(), "STEPS", start, 1000.0),
                        createStatisticsData(user2.getUserId(), "STEPS", start.plusDays(1), 500.0)
                ));

        when(statisticsRepository.findByUserIdAndNameAndTimestampBetween(
                userId, "STEPS", start, end))
                .thenReturn(Collections.singletonList(
                        createStatisticsData(userId, "STEPS", start, 750.0)
                ));

        List<Pair<User, Double>> result =
                statisticsCoreService.getLeaderBoard(userId, start, end);

        assertEquals(3, result.size(), "Should have 3 entries");

        assertEquals(user2.getUserId(), result.get(0).getFirst().getUserId(), "User2 should be first");
        assertEquals(1500.0, result.get(0).getSecond(), 0.001, "User2 total steps should be 1500");

        assertEquals(userId, result.get(1).getFirst().getUserId(), "Main user should be second");
        assertEquals(750.0, result.get(1).getSecond(), 0.001, "Main user total steps should be 750");

        assertEquals(user1.getUserId(), result.get(2).getFirst().getUserId(), "User1 should be third");
        assertEquals(500.0, result.get(2).getSecond(), 0.001, "User1 total steps should be 500");
    }

    private UserStatisticsData createStatisticsData(UUID userId, String name, LocalDate date, double value) {
        UserStatisticsData.UserStatisticsKey key = UserStatisticsData.UserStatisticsKey.builder()
                .userId(userId)
                .name(name)
                .timestamp(date)
                .build();
        return UserStatisticsData.builder()
                .key(key)
                .value(value)
                .build();
    }
}