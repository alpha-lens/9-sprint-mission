package com.sprint.mission.discodeit.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final UUID id;
    private final Long createAt;
    private String name;
    private String pw;
    private String email;
    private String phoneNumber;
    private Long updateAt;
    private static final long serialVersionUID = 1L;

    public User(String name, String pw) {
        long n = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.name = name;
        this.pw = pw;
        this.createAt = n;
        this.updateAt = n;
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPw() {
        return this.pw;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public long getCreateAt() {
        return this.createAt;
    }

    public long getUpdateAt() {
        return this.updateAt;
    }

    /// set부분 수정하기
    /// sdf

    public void setUpdateAt() {
        this.updateAt = System.currentTimeMillis();
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
        this.pw = password;
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
        return "====================\n"
                + "사용자ID : " + this.getId()
                + "사용자명 : " + this.getName()
                + "이메일 : " + this.getEmail()
                + "전화번호 : " + this.getPhoneNumber()
                + "생성일 : " + sdf.format(new Date(this.getCreateAt()))
                + "수정일 : " + sdf.format(new Date(this.getUpdateAt()));
    }
}
