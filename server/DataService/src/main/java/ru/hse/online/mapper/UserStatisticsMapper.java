package ru.hse.online.mapper;

import ru.hse.online.model.UserStatistics;
import ru.hse.online.storage.UserStatisticsData;

public class UserStatisticsMapper {

    public static UserStatistics toModel(UserStatisticsData data) {
        if (data == null) {
            return null;
        }

        return UserStatistics.builder()
                .userId(data.getKey().getUserId())
                .name(data.getKey().getName())
                .timestamp(data.getKey().getTimestamp())
                .value(data.getValue())
                .build();
    }

    public static UserStatisticsData toData(UserStatistics statistics) {
        if (statistics == null) {
            return null;
        }

        UserStatisticsData.UserStatisticsKey key = UserStatisticsData.UserStatisticsKey.builder()
                .userId(statistics.getUserId())
                .name(statistics.getName())
                .timestamp(statistics.getTimestamp())
                .build();

        return UserStatisticsData.builder()
                .key(key)
                .value(statistics.getValue())
                .build();
    }
}
