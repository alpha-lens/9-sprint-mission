package com.sprint.mission.discodeit.entity.service.user;

import com.sprint.mission.discodeit.entity.UUIDGenerate;

import java.time.LocalDate;

public class User {
    LocalDate now = LocalDate.now();

    private String id;
    private long createAt;
    private long updateAt;
    private String name;
    private String profilePhoto; /// 사진 경로 사용. 아직은 모르니 패스
    private String details; /// 상세 설명

    public User(String name) {
        this.id = new UUIDGenerate().toString();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.name = name;
    }

    public long getCreateAt() {
        return createAt;
    }
    public long getUpdateAt() {
        return updateAt;
    }
    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }
}
