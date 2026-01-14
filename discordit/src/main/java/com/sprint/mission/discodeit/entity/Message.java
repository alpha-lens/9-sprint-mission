package com.sprint.mission.discodeit.entity;

import java.util.HashMap;
import java.util.UUID;

public class Message {
    /// Test:
    /// HashMap을 이용해서 {mId, chId}, {mId, uId}를 묶고
    /// {mId : 나머지 내용} 형태로 묶어볼까?
    private final HashMap<UUID, UUID> mchId = new HashMap<>();
    private final HashMap<UUID, UUID> muId = new HashMap<>();
    private final UUID id;
    private final Long createAt;
    private UUID channelId;
    private UUID userId;
    private Long updateAt;
    private String content;

    public Message(UUID channelId, UUID userId, String content){
        long n = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.userId = userId;
        this.content = content;
        this.createAt = n;
        this.updateAt = n;
    }

    public Message(String content){
        long n = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.content = content;
        this.createAt = n;
        this.updateAt = n;
    }

    public UUID getId(){return this.id;};
    public UUID getSendChannel(){return this.channelId;};
    public UUID getSendUserId(){return this.userId;};
    public String getContent() {return this.content;}
    public void setContent(String content) {this.content = content;setUpdateAt();}
    public long getUpdateAt(){return this.updateAt;};
    public long getCreateAt(){return this.createAt;};
    public void setUpdateAt(){this.updateAt = System.currentTimeMillis();};
}
