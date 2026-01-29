package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
            .withZone(ZoneId.of("Asia/Seoul"));

    private final UUID id;
    private final Instant createAt;
    private final String createUser;
    private final Map<String, UUID> accessUser = new ConcurrentHashMap<>();
    private final ChannelType channelType;
    private String name;
    private Instant updateAt;

    public Channel(String name, String createUser, UUID createUserId) {
        this(name, createUser, createUserId, ChannelType.PUBLIC);
    }

    public Channel(String name, String createUser, UUID createUserId, ChannelType channelType) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.name = name;
        this.createUser = createUser;
        this.channelType = channelType;
        this.createAt = now;
        this.updateAt = now;
        accessUser.put(createUser, createUserId);
    }

    /// setter
    private void setUpdateAt() {
        this.updateAt = Instant.now();
    }

    private void setName(String name) {
        this.name = name;
        setUpdateAt();
    }

    public void channelUpdater(String name) {
        setName(name);
    }

    @Override
    public String toString() {
        return "====================\n" +
                "채널명 : " + this.getName() + "\n" +
                "타입 : " + this.getChannelType() + "\n" + // 타입 정보 추가
                "채널ID : " + this.getId() + "\n" +
                "채널수정일 : " + formatter.format(this.getUpdateAt()) + "\n" +
                "채널생성일 : " + formatter.format(this.getCreateAt()) + "\n" +
                "====================\n";
    }
}