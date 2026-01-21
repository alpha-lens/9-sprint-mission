package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelRepository {
    boolean save(Channel channel);
    boolean save(String oldName, String newName);
    Channel readChannel(String name);

    List<Channel> readAllChannel();

    boolean deleteChannel(String name);
}
