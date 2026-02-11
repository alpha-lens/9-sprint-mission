package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.FindChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    boolean isPresent(UUID id);
    UUID create(String type, String name, String createUserName);
    FindChannelDto find(UUID channelId, UUID userId);

    List<ResponseChannelDto> findAllPrivateChannel(UUID userId);

    List<ResponseChannelDto> findAll(UUID userId);
    boolean update(UpdateChannelDto requestDto);
    boolean delete(UUID id);

    void deleteAll(String name);

    boolean includePrivateChannel(String channelName, String userName, UUID userId);

    void excludePrivateChannel(String channelName, String userName);

    boolean isCreatePrivateChannel(String userName);

    boolean findChannelCreator(UUID id, String userName);
}
