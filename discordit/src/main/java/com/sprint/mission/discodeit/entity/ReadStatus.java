package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
        id = UUID.randomUUID();
        lastReadAt = Instant.now();
    };

    public void updateReadAt() {
        lastReadAt = Instant.now();
    }
}
