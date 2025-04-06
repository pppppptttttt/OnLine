package ru.hse.online.service.core;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hse.online.mapper.UserStatisticsMapper;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.repository.UserStatisticsRepository;
import ru.hse.online.service.StatisticsService;
import ru.hse.online.storage.UserStatisticsData;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticsCoreService implements StatisticsService {
    private final UserStatisticsRepository userStatisticsRepository;

    public List<UserStatistics> getStatisticsForPeriod(UUID userId, String name, LocalDate start, LocalDate end) {
        return userStatisticsRepository.findByUserIdAndNameAndTimestampBetween(
                        userId,
                        name,
                        start,
                        end
                ).stream()
                .map(UserStatisticsMapper::toModel)
                .collect(Collectors.toList());
    }

    public void saveStatistics(List<UserStatistics> userStats) {
        List<UserStatisticsData> dataList = userStats.stream()
                .map(UserStatisticsMapper::toData)
                .collect(Collectors.toList());
        userStatisticsRepository.saveAll(dataList);
    }
}
