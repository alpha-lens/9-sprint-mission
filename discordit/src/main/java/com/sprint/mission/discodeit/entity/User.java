package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String pw;
    private String email;
    private String phonenumber;
    private long createAt;
    private long updateAt;

    public User(String name, String pw) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.pw = pw;
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public UUID getId() {return this.id;}
    public String getName() {return this.name;}
    public String getPw() {return this.pw;}
    public String getEmail() {return this.email;}
    public String getPhonenumber() {return this.phonenumber;}
    public long getCreateAt() {return this.createAt;}
    public long getUpdateAt() {return this.updateAt;}
    public void setUpdateAt() {this.updateAt = System.currentTimeMillis();}
    public void setPw(String pw) {this.pw = pw;setUpdateAt();}
    public void setEmail(String Email) {this.email = email;setUpdateAt();}
    public void setName(String name) {this.name = name;setUpdateAt();}
    public void setPhonenumber(String phonenumber) {this.phonenumber = phonenumber;setUpdateAt();}
}
