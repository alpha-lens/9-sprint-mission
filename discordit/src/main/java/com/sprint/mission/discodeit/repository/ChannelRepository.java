package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    UUID save(CreateChannelDto requestDto);
    boolean save(UpdateChannelDto requestDto);
    ResponseChannelDto findChannel(UUID channelId, UUID userId);

    ChannelType getChannelType(UUID id);

    UUID getChannelId(String name);

    List<ResponseChannelDto> findAllChannel(UUID userId);

    List<ResponseChannelDto> findAllPrivateChannel(UUID userId);

    List<ResponseChannelDto> accessiblePrivateChannel(UUID userId);

    ResponseChannelDto requestChannelInfo(Channel channel);

    void includePrivateChannel(String channelName, String userName, UUID userId);

    void excludePrivateChannel(String channelName, String userName);

    boolean deleteChannel(UUID id);

    void deleteAllChannel(String name);

    boolean isPresentChannel(UUID id);

    boolean isCreatePrivateChannel(String name);

    boolean findChannelCreator(UUID id, String userName);

    UUID channelNameToId(String name);

    String channelIdToName(UUID id);
}
