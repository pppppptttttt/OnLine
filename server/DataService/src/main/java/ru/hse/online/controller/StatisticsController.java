package ru.hse.online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hse.online.model.User;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.service.FriendshipService;
import ru.hse.online.service.StatisticsService;
import ru.hse.online.service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final FriendshipService friendshipService;
    private final UserService userService;

    @Operation(summary = "Get statistics data")
    @GetMapping("get/statistics/")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserStatistics.class))))
    })
    public ResponseEntity<List<UserStatistics>> getStatistics(
            @Parameter(description = "User ID")
            @RequestParam(name = "userId")
            UUID userId,
            @Parameter(description = "Metric name")
            @RequestParam(name = "name")
            String name,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam(name = "start")
            String startStr,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam(name = "end")
            String endStr) {
        LocalDate start = LocalDate.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ResponseEntity.ok(
                statisticsService.getStatisticsForPeriod(userId, name, start, end)
        );
    }

    public ResponseEntity<List<Pair<User, Double>>> getLeaderBoard(
            @Parameter(description = "User ID")
            @RequestParam(name = "userId")
            UUID userId,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam(name = "start")
            String startStr,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam(name = "end")
            String endStr) {
        LocalDate start = LocalDate.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate end = LocalDate.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<User> boardIds = friendshipService.getFriendsList(userId);
        boardIds.add(userService.getUserById(userId));


        List<Pair<User, Double>> leaderBoard = boardIds.stream()
                .map(user -> {
                    List<UserStatistics> stats = statisticsService.getStatisticsForPeriod(user.getUserId(), "STEPS", start, end);
                    double totalSteps = stats.stream()
                            .mapToDouble(UserStatistics::getValue)
                            .sum();

                    return Pair.of(user, totalSteps);
                })
                .sorted((entry1, entry2) -> -Double.compare(entry2.getSecond(), entry1.getSecond()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderBoard);
    }

    @PostMapping("create/statistics/")
    public ResponseEntity<Void> addStatistics(
            @Valid @RequestBody List<UserStatistics> stats
    ) {
        statisticsService.saveStatistics(stats);
        return ResponseEntity.noContent().build();
    }
}