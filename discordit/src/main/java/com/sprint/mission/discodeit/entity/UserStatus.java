package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@EntityScan
public class UserStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final String userName;
    private final Instant createAt;
    private Instant updateAt;

    public UserStatus(UUID userId, String userName) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.userId = userId;
        this.userName = userName;
        this.updateAt = now;
        this.createAt = now;
    }

    public void lastAccessTimeUpdater() {
        updateAt = Instant.now();
    }

    public void lastAccessTimeUpdater(Instant time) {
        updateAt = time;
    }

    public boolean isOnline() {
        try {
            Duration duration = Duration.between(this.getUpdateAt(), Instant.now());
            return duration.toMinutes() <= 5;
        } catch (Exception ignore) {
            return false;
        }
    }
}
