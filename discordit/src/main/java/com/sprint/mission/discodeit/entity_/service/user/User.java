package com.sprint.mission.discodeit.entity_.service.user;

import com.sprint.mission.discodeit.entity_.UUIDGenerate;

import java.time.LocalDate;
import java.util.List;

public class User {
    public static boolean duplicateCheck(List<User> list, String name) {
        int temp = list.stream().filter(e -> e.getName().equals(name)).toArray().length;

        return temp == 0;
    }
    LocalDate now = LocalDate.now();

    private final String id;
    private final long createAt;
    private long updateAt;
    private String name;
    private String pw;
    private String profilePhoto; /// 사진 경로 사용. 아직은 모르니 패스
    private String details; /// 상세 설명
    private String email;
    private String phoneNumber;

    public User(String name, String pw, String email, String phoneNumber) {
        this.id = new UUIDGenerate().toString();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.pw = pw;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public long getCreateAt() {
        return createAt;
    }
    public long getUpdateAt() {
        return updateAt;
    }
    public String getEmail() {return this.email;}
    public String getName() {return this.name;}
    public String getPw() {return this.pw;}
    public String getPhoneNumber() {return this.phoneNumber;}
    public String getId() {return this.id;}
    public void setEmail(String email) {
        this.email = email;
        this.updateAt = System.currentTimeMillis();
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.updateAt = System.currentTimeMillis();
    }
    public void setName(String name) {
        this.name = name;
        this.updateAt = System.currentTimeMillis();
    }
}
