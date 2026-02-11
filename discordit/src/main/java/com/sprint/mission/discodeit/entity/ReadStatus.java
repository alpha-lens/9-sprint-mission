package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@EntityScan
public class ReadStatus implements Serializable {
    private final UUID id = UUID.randomUUID();
    private final UUID userId;
    private final UUID channelId;
    private final Instant createAt;
    private Instant updateAt;

    public ReadStatus(UUID userId, UUID channelId){
        Instant now = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
        createAt = now;
        updateAt = now;
    }

    public void updateReadAt() {
        updateAt = Instant.now();
    }
}
