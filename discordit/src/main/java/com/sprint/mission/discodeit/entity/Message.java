package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID id;
    private final Long createAt;
    private final UUID channelId;
    private final UUID userId;
    private Long updateAt;
    private String content;
    private static final long serialVersionUID = 1L;

    public Message(UUID channelId, UUID userId, String content) {
        long n = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createAt = n;
        this.updateAt = n;
    }

    public UUID getId() {
        return this.id;
    }

    public UUID getSendChannel() {
        return this.channelId;
    }

    public UUID getSenderUserId() {
        return this.userId;
    }

    public String getContent() {
        return this.content;
    }

    public long getUpdateAt() {
        return this.updateAt;
    }

    public long getCreateAt() {
        return this.createAt;
    }

    public void updateMessage(String content) {
        setContent(content);
    }

    private void setContent(String content) {
        this.content = content;
        setUpdateAt();
    }

    private void setUpdateAt() {
        this.updateAt = System.currentTimeMillis();
    }
}
