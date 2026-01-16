package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createAt;
    private String name;
    private Long updateAt;

    public Channel(String name) {
        long n = System.currentTimeMillis();
        this.name = name;
        this.id = UUID.randomUUID();
        this.createAt = n;
        this.updateAt = n;
    }

    /// getter
    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public long getCreateAt() {
        return this.createAt;
    }

    public long getUpdateAt() {
        return this.updateAt;
    }

    /// setter
    public void setUpdateAt() {
        this.updateAt = System.currentTimeMillis();
    }

    public void setName(String name) {
        this.name = name;
        setUpdateAt();
    }
}
