package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id = UUID.randomUUID();
    private final UUID relationId;
    private final AttachmentType type;
    private final Instant createAt;
    private final String fileName;
    private final String fileExtension;

    public BinaryContent(AttachmentType type, UUID relationId,String fileName) {
        this.createAt = Instant.now();
        this.fileName = fileName.split("\\.")[0];
        this.fileExtension = fileName.split("\\.")[1];
        this.type = type;
        this.relationId = relationId;
    }

    @Override
    public String toString() {
        return "        BinaryContent ID : " + id
                + "        fileName : " + fileName + "." + fileExtension + "\n";
    }
}
