package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Channel implements Serializable {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final UUID id;
    private final Long createAt;
    private String name;
    private Long updateAt;
    private static final long serialVersionUID = 1L;

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
    private void setUpdateAt() {
        this.updateAt = System.currentTimeMillis();
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
                "채널수정일 : " + sdf.format(new Date(this.getUpdateAt())) + "\n" +
                "채널생성일 : " + sdf.format(new Date(this.getCreateAt())) + "\n" +
                "====================\n";
    }
}
