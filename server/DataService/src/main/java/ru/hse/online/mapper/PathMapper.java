package ru.hse.online.mapper;

import ru.hse.online.model.Path;
import ru.hse.online.storage.PathData;

public class PathMapper {

    public static Path toModel(PathData pathData) {
        if (pathData == null) {
            return null;
        }

        return Path.builder()
                .userId(pathData.getKey().getUserId())
                .pathId(pathData.getKey().getPathId())
                .polyline(pathData.getPolyline())
                .created(pathData.getCreated())
                .name(pathData.getName())
                .distance(pathData.getDistance())
                .duration(pathData.getDuration())
                .build();
    }

    public static PathData toData(Path path) {
        if (path == null) {
            return null;
        }

        PathData.PathKey key = PathData.PathKey.builder()
                .userId(path.getUserId())
                .pathId(path.getPathId())
                .build();

        return PathData.builder()
                .key(key)
                .polyline(path.getPolyline())
                .created(path.getCreated())
                .name(path.getName())
                .distance(path.getDistance())
                .duration(path.getDuration())
                .build();
    }
}
