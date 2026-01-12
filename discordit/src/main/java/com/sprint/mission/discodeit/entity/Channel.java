package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private long createAt;
    private long updateAt;
    HashMap<UUID, String> message = new HashMap<UUID, String>();

    public Channel(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    /// getter
    public UUID getId() {return this.id;}
    public String getName() {return this.name;}
    public long getCreateAt() {return this.createAt;}
    public long getUpdateAt() {return this.updateAt;}
    public HashMap<UUID, String> getMessage() {
        return this.message;
    }

    /// setter
    public void setUpdateAt() {this.updateAt = System.currentTimeMillis();}
    public void setName(String name) {
        this.name = name;
        setUpdateAt();
    }
}
