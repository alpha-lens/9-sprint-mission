package com.sprint.mission.discodeit.entity_.service;

public class GeneralCreater {
    private String id;
    private long createAt;
    private long updateAt;
    private String name;

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
}
