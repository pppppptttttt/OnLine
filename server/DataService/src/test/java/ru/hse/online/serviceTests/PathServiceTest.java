package ru.hse.online.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hse.online.mapper.PathMapper;
import ru.hse.online.model.Path;
import ru.hse.online.repository.PathRepository;
import ru.hse.online.service.core.PathCoreService;
import ru.hse.online.storage.PathData;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private PathRepository pathRepository;

    @InjectMocks
    private PathCoreService pathCoreService;

    private UUID userId;
    private UUID pathId1;
    private Path path1;
    private Path path2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        pathId1 = UUID.randomUUID();
        UUID pathId2 = UUID.randomUUID();

        path1 = Path.builder()
                .userId(userId)
                .pathId(pathId1)
                .polyline("polyline1")
                .created(LocalDate.now())
                .name("Test Path 1")
                .distance(100.0)
                .duration(60.0)
                .build();

        path2 = Path.builder()
                .userId(userId)
                .pathId(pathId2)
                .polyline("polyline2")
                .created(LocalDate.now())
                .name("Test Path 2")
                .distance(200.0)
                .duration(120.0)
                .build();
    }

    @Test
    void getPathsListReturnsListOfPaths() {
        PathData pathData1 = PathMapper.toData(path1);
        PathData pathData2 = PathMapper.toData(path2);

        when(pathRepository.findByUserId(userId))
                .thenReturn(Arrays.asList(pathData1, pathData2));

        List<Path> paths = pathCoreService.getPathsList(userId);

        assertEquals(2, paths.size());
        assertEquals(path1, paths.get(0));
        assertEquals(path2, paths.get(1));
    }

    @Test
    void addPathCallsSaveMethod() {
        pathCoreService.addPath(path1);
        verify(pathRepository).save(Mockito.any(PathData.class));
    }

    @Test
    void removePathCallsDeleteMethod() {
        pathCoreService.removePath(userId, pathId1);
        verify(pathRepository).deleteByUserIdAndPathId(userId, pathId1);
    }
}
