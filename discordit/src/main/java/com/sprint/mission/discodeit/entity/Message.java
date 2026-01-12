package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private UUID channelId;
    private UUID userId;
    private long createAt;
    private long updateAt;
    private String content;

    public Message(UUID channelId, UUID userId, String content){
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public Message(String content){
        this.id = UUID.randomUUID();
        this.content = content;
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public UUID getId(){return this.id;};
    public long getUpateAt(){return this.updateAt;};
    public long getCreateAt(){return this.createAt;};
    public void setUpdateAt(){this.updateAt = System.currentTimeMillis();};
    public void editMessage(String msg){
        this.content = msg;
        setUpdateAt();
    };
    public void sendMessage(String content){};
}
