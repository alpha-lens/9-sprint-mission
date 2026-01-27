package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
            .withZone(ZoneId.of("Asia/Seoul"));

    private final UUID id;
    private final Instant createAt;
    private String name;
    private Instant updateAt;
    @Serial
    private static final long serialVersionUID = 1L;

    public Channel(String name) {
        Instant n = Instant.now();
        this.name = name;
        this.id = UUID.randomUUID();
        this.createAt = n;
        this.updateAt = n;
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
                "채널ID : " + this.getId() + "\n" +
                "채널수정일 : " + formatter.format(this.getUpdateAt()) + "\n" +
                "채널생성일 : " + formatter.format(this.getCreateAt()) + "\n" +
                "====================\n";
    }
}
