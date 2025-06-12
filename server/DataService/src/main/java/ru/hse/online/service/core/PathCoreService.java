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
        UUID pathIdToSet = path.getPathId() == null ? UUID.randomUUID() : path.getPathId();
        path.setPathId(pathIdToSet);

        PathData pathDataToSave = PathMapper.toData(path);
        pathRepository.save(pathDataToSave);
    }

    @Transactional
    public void removePath(UUID userId, UUID pathId) {
        pathRepository.deleteByUserIdAndPathId(userId, pathId);
    }
}