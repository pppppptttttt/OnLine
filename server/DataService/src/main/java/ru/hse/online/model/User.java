package ru.hse.online.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "User entity")
public class User {
    @NotNull
    @Schema(description = "Unique user ID", example = "770e8400-e29b-41d4-a716-446655440000")
    private final UUID userId;

    @Schema(description = "Username", example = "anton")
    private final String username;

    @NotNull
    @Email
    @Schema(description = "Email", example = "java.enjoyer@gmail.com")
    private final String email;

    @Schema(description = "List of friend IDs")
    private final List<UUID> friends;
}
