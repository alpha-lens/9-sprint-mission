package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelRepository {
    boolean save(Channel channel);
    boolean save(String oldName, String newName);
    String readChannel(String name);

    List<ResponseChannelDto> readAllChannel();

    boolean deleteChannel(String name);
}
