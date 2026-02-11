package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseFindChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    boolean isPresent(UUID id);
    ResponseChannelDto create(String type, String name, UUID userId);
    ResponseFindChannelDto find(UUID channelId, UUID userId);

    List<ResponseChannelDto> findAllPrivateChannel(UUID userId);

    List<ResponseChannelDto> findAll(UUID userId);
    ResponseChannelDto update(RequestUpdateChannelDto requestDto);
    boolean delete(UUID id);

    void deleteAll(String name);

    boolean includePrivateChannel(String channelName, String userName, UUID userId);

    void excludePrivateChannel(String channelName, String userName);

    boolean isCreatePrivateChannel(String userName);

    boolean findChannelCreator(UUID id, String userName);
}
