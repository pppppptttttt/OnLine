package ru.hse.online.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Table("paths")
public class PathData {
    @NotNull
    @PrimaryKey
    private final PathKey key;

    @NotNull
    @Column("polyline")
    private final String polyline;

    @Data
    @Builder
    @PrimaryKeyClass
    public static class PathKey implements Serializable {
        @PrimaryKeyColumn(name = "user_id", type = PrimaryKeyType.PARTITIONED)
        private final UUID userId;

        @PrimaryKeyColumn(name = "path_id", type = PrimaryKeyType.CLUSTERED)
        private final UUID pathId;
    }
}
