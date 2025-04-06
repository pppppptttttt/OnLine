package ru.hse.online.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Path entity")
public class Path {
    @NotNull
    @Schema(description = "Path's userId")
    private final UUID userId;

    @NotNull
    @Schema(description = "Path's Id")
    private final UUID pathId;

    @NotNull
    @Schema(description = "Polyline coordinates")
    private final String polyline;
}

