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
import ru.hse.online.service.StatisticsService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

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

    @Operation(summary = "Get leaderboard of user and friends by total steps")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Object.class))))
    })
    @GetMapping("get/leaderboard/")
    public ResponseEntity<List<Pair<User, Double>>> getLeaderBoard(
            @Parameter(description = "User ID") @RequestParam(name = "userId") UUID userId,
            @Parameter(description = "Start date (YYYY-MM-DD)") @RequestParam(name = "start") String start,
            @Parameter(description = "End date (YYYY-MM-DD)") @RequestParam(name = "end") String end) {

        LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ISO_DATE);
        LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ISO_DATE);

        return ResponseEntity.ok(
                statisticsService.getLeaderBoard(userId, startDate, endDate)
        );
    }

    @PostMapping("create/statistics/")
    public ResponseEntity<Void> addStatistics(
            @Valid @RequestBody List<UserStatistics> stats
    ) {
        statisticsService.saveStatistics(stats);
        return ResponseEntity.noContent().build();
    }
}