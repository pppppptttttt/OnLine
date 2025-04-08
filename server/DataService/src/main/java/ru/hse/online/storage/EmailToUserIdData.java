package ru.hse.online.storage;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@Builder
@Table("email_to_user_id")
public class EmailToUserIdData {
    @NotNull
    @PrimaryKey("email")
    private final String email;

    @NotNull
    @Column("user_id")
    private final UUID userId;
}
