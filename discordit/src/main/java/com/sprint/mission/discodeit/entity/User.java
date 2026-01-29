package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
            .withZone(ZoneId.of("Asia/Seoul"));
    private final UUID id;
    private UUID profileId;
    private final Instant createAt;
    private Instant updateAt;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    @Serial
    private static final long serialVersionUID = 1L;

    public User(String name, String password) {
        Instant now = Instant.now();
        this.id = UUID.randomUUID();
        this.name = name;
        this.password = password;
        this.createAt = now;
        this.updateAt = now;
    }

    public User(String name, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.profileId = profileId;
        this.name = name;
        this.password = password;
        Instant now = Instant.now();
        this.createAt = now;
        this.updateAt = now;
    }

    public void setUpdateAt() {
        this.updateAt = Instant.now();
    }

    public void updateUser(String name, String password, String email, String phoneNumber) {
        /// null checker
        boolean[] argumentsList = {check(name), check(password), check(email), check(phoneNumber)};
        if(argumentsList[0]) setName(name);
        if(argumentsList[1]) setPassword(password);
        if(argumentsList[2]) setEmail(email);
        if(argumentsList[3]) setPhoneNumber(phoneNumber);
    }

    private boolean check(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private void setName(String name) {
        this.name = name;
        setUpdateAt();
    }

    private void setPassword(String password) {
        this.password = password;
        setUpdateAt();
    }

    private void setEmail(String email) {
        this.email = email;
        setUpdateAt();
    }

    private void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return "===================="
                + "\n사용자ID : " + this.getId()
                + "\n사용자명 : " + this.getName()
                + "\n이메일 : " + this.getEmail()
                + "\n전화번호 : " + this.getPhoneNumber()
                + "\n생성일 : " + FORMATTER.format(this.getCreateAt())
                + "\n수정일 : " + FORMATTER.format(this.getUpdateAt());
    }
}
