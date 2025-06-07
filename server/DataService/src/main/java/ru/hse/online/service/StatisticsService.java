package ru.hse.online.service;

import org.springframework.data.util.Pair;
import ru.hse.online.model.User;
import ru.hse.online.model.UserStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StatisticsService {
    List<UserStatistics> getStatisticsForPeriod(UUID userId, String name, LocalDate start, LocalDate end);

    void saveStatistics(List<UserStatistics> UserStats);

    List<Pair<User, Double>> getLeaderBoard(UUID userId, LocalDate start, LocalDate end);
}
