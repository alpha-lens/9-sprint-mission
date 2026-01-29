package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id = UUID.randomUUID();
    private final Instant createAt;
    private final String fileName;
    private final String fileExtension;

    public BinaryContent(String fileName) {
        this.createAt = Instant.now();
        this.fileName = fileName.split("\\.")[0];
        this.fileExtension = fileName.split("\\.")[1];
    }
}
