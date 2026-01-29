package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final UUID channelId;
    private Instant readAt;

    public ReadStatus(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
        readAt = Instant.now();
    };

    public void updateReadAt() {
        readAt = Instant.now();
    }
}
