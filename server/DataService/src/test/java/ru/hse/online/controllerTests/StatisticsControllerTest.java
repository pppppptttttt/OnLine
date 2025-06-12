package ru.hse.online.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hse.online.controller.StatisticsController;
import ru.hse.online.model.User;
import ru.hse.online.model.UserStatistics;
import ru.hse.online.service.StatisticsService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StatisticsService statisticsService;

    @Test
    @DisplayName("GET /api/v1/get/statistics/ - Select by Period")
    void getStatisticsByPeriod() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 2, 1);

        UserStatistics expectedStat = UserStatistics.builder()
                .value(100.0)
                .userId(userId)
                .name("steps")
                .timestamp(start)
                .build();

        when(statisticsService.getStatisticsForPeriod(
                eq(userId), eq("steps"), eq(start), eq(end)))
                .thenReturn(List.of(expectedStat));

        mockMvc.perform(get("/api/v1/get/statistics/")
                        .param("userId", userId.toString())
                        .param("name", "steps")
                        .param("start", String.valueOf(start))
                        .param("end", String.valueOf(end)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value(100.0));
    }

    @Test
    @DisplayName("POST /api/v1/create/statistics/ - Empty List")
    void addStatisticsEmptyList() throws Exception {
        mockMvc.perform(post("/api/v1/create/statistics/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isNoContent());

        verify(statisticsService).saveStatistics(argThat(List::isEmpty));
    }

    @Test
    @DisplayName("GET /api/v1/get/leaderboard - Success")
    void getLeaderBoardSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        User user = User.builder()
                .userId(userId)
                .username("testUser")
                .email("test@example.com")
                .friends(Collections.emptyList())
                .build();

        Pair<User, Double> entry = Pair.of(user, 1500.0);

        when(statisticsService.getLeaderBoard(
                eq(userId),
                eq(start),
                eq(end))
        ).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/v1/get/leaderboard")
                        .param("userId", userId.toString())
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].first.userId").value(userId.toString()))
                .andExpect(jsonPath("$[0].first.username").value("testUser"))
                .andExpect(jsonPath("$[0].first.email").value("test@example.com"))
                .andExpect(jsonPath("$[0].second").value(1500.0));
    }

    @Test
    @DisplayName("GET /api/v1/get/leaderboard - Empty Result")
    void getLeaderBoardEmpty() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        when(statisticsService.getLeaderBoard(
                eq(userId),
                eq(start),
                eq(end))
        ).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/get/leaderboard")
                        .param("userId", userId.toString())
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}