package ru.hse.online.service.core;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.online.mapper.PathMapper;
import ru.hse.online.model.Path;
import ru.hse.online.repository.PathRepository;
import ru.hse.online.service.PathService;
import ru.hse.online.storage.PathData;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PathCoreService implements PathService {
    private final PathRepository pathRepository;

    public List<Path> getPathsList(UUID userId) {
        return pathRepository.findByUserId(userId)
                .stream()
                .map(PathMapper::toModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addPath(Path path) {
        PathData pathData = PathMapper.toData(path);
        if (pathData.getKey().getPathId() == null) {
            PathData.PathKey newKey = PathData.PathKey.builder()
                    .userId(pathData.getKey().getUserId())
                    .pathId(UUID.randomUUID())
                    .build();
            pathData = PathData.builder()
                    .key(newKey)
                    .polyline(pathData.getPolyline())
                    .build();
        }
        pathRepository.save(pathData);
    }

    @Transactional
    public void removePath(UUID userId, UUID pathId) {
        pathRepository.deleteByUserIdAndPathId(userId, pathId);
    }
}