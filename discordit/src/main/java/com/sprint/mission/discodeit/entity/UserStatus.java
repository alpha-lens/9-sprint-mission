package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private Instant lastAccessTime;

    public UserStatus(UUID userId) {
        this.userId = userId;
        lastAccessTime = Instant.now();
    }

    public void lastAccessTimeUpdater() {
        lastAccessTime = Instant.now();
    }

    @Override
    public String toString() {
        try {
            Duration duration = Duration.between(this.getLastAccessTime(), Instant.now());
            if(duration.toMinutes() > 5) return "오프라인";
            return "온라인";
        } catch (Exception ignore) {
            return "오프라인";
        }
    }
}
