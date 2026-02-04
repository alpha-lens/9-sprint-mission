package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;

public interface ChannelRepository {
    boolean save(Channel channel);
    boolean save(UpdateChannelDto requestDto);
    String readChannel(String name);

    List<ResponseChannelDto> readAllChannel(String userName);

    List<ResponseChannelDto> findAllPrivateChannel(String userName);

    boolean deleteChannel(String name);
}
