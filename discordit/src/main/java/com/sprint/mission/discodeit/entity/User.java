package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createAt;
    private String name;
    private String pw;
    private String email;
    private String phonenumber;
    private Long updateAt;

    public User(String name, String pw) {
        long n = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.name = name;
        this.pw = pw;
        this.createAt = n;
        this.updateAt = n;
    }

    public UUID getId() {return this.id;}
    public String getName() {return this.name;}
    public String getPw() {return this.pw;}
    public String getEmail() {return this.email;}
    public String getPhonenumber() {return this.phonenumber;}
    public long getCreateAt() {return this.createAt;}
    public long getUpdateAt() {return this.updateAt;}
    public void setUpdateAt() {this.updateAt = System.currentTimeMillis();}
    public void setPw(String pw) {
        this.pw = pw;
        setUpdateAt();
    }
    public void setEmail(String email) {this.email = email;setUpdateAt();}
    public void setName(String name) {this.name = name;setUpdateAt();}
    public void setPhonenumber(String phonenumber) {this.phonenumber = phonenumber;setUpdateAt();}
}
