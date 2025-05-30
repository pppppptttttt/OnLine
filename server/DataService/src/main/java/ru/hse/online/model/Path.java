package ru.hse.online.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Path entity")
public class Path {
    @NotNull
    @Schema(description = "Path's userId")
    private UUID userId;

    @NotNull
    @Schema(description = "Path's Id")
    private UUID pathId;

    @NotNull
    @Schema(description = "Polyline coordinates")
    private String polyline;

    @NotNull
    @Schema(description = "Creation timestamp")
    private LocalDate created;

    @Schema(description = "Name of the path", nullable = true)
    private String name;

    @Schema(description = "Distance of the path in meters", nullable = true)
    private Double distance;

    @Schema(description = "Duration of the path in seconds", nullable = true)
    private Double duration;
}

