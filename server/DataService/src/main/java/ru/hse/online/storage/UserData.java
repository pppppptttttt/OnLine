package ru.hse.online.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Table("users")
public class UserData {
    @NotNull
    @PrimaryKey("user_id")
    private final UUID userId;

    @Column("username")
    private final String username;

    @NotNull
    @Column("email")
    private final String email;

    @Column("friends")
    private final List<UUID> friends;
}