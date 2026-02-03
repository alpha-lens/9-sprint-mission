package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.FindChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    boolean isPresent(String name);
    boolean create(String type, String name);
    FindChannelDto find(String name);

    List<ResponseChannelDto> findAllPrivateChannel(String userName);

    List<ResponseChannelDto> findAll(String userName);
    boolean update(String oldName, String newName);
    boolean delete(String name);

    boolean invitePrivateServer(String channelName, String userName, UUID userId);
}
