package ru.hse.online.service.core;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.hse.online.mapper.UserStatisticsMapper;
import ru.hse.online.model.User;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.repository.UserStatisticsRepository;
import ru.hse.online.service.FriendshipService;
import ru.hse.online.service.StatisticsService;
import ru.hse.online.service.UserService;
import ru.hse.online.storage.UserStatisticsData;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticsCoreService implements StatisticsService {
    private final UserStatisticsRepository userStatisticsRepository;
    private final FriendshipService friendshipService;
    private final UserService userService;

    public List<UserStatistics> getStatisticsForPeriod(UUID userId, String name, LocalDate start, LocalDate end) {
        return userStatisticsRepository.findByUserIdAndNameAndTimestampBetween(
                        userId,
                        name,
                        start,
                        end
                ).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public void saveStatistics(List<UserStatistics> userStats) {
        List<UserStatisticsData> dataList = userStats.stream()
                .map(this::toData)
                .collect(Collectors.toList());
        userStatisticsRepository.saveAll(dataList);
    }

    @Override
    public List<Pair<User, Double>> getLeaderBoard(UUID userId, LocalDate start, LocalDate end) {
        List<User> boardUsers = friendshipService.getFriendsList(userId);
        boardUsers.add(userService.getUserById(userId));

        return boardUsers.stream()
                .map(user -> {
                    List<UserStatistics> stats = getStatisticsForPeriod(
                            user.getUserId(), "STEPS", start, end
                    );
                    double totalSteps = stats.stream()
                            .mapToDouble(UserStatistics::getValue)
                            .sum();
                    return Pair.of(user, totalSteps);
                })
                .sorted((entry1, entry2) ->
                        Double.compare(entry2.getSecond(), entry1.getSecond())
                )
                .collect(Collectors.toList());
    }

    private UserStatistics toModel(UserStatisticsData data) {
        return UserStatisticsMapper.toModel(data);
    }

    private UserStatisticsData toData(UserStatistics statistics) {
        return UserStatisticsMapper.toData(statistics);
    }
}