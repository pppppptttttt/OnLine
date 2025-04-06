package ru.hse.online.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class UserStatistics {
    @NotNull
    private final UUID userId;

    @NotNull
    private final String name;

    @NotNull
    private final LocalDate timestamp;

    @NotNull
    @Schema(example = "100")
    private final Double value;
}

