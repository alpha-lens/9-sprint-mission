package com.sprint.mission.discodeit.entity_.service.channel;

import com.sprint.mission.discodeit.entity_.UUIDGenerate;

import java.util.Scanner;

public class Channel {
    private String id;
    private final long createAt;
    private long updateAt;
    private String name;
    Scanner sc = new Scanner(System.in);

    public Channel(String name) {
        this.id = new UUIDGenerate().toString();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.name = name;
    }
    public String getId() { return id; }
    public long getCreateAt() {
        return createAt;
    }
    public long getUpdateAt() {
        return updateAt;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public String generateChannel() {
        System.out.println("지금은 채널을 만들려고 합니다!");
        System.out.println("만들고자 하는 채널명은 무엇인가요?");
        return sc.nextLine();
    }
}