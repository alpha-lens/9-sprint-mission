package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestCreateChannelDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

  ResponseChannelDto save(RequestCreateChannelDto requestDto);

  ResponseChannelDto save(RequestUpdateChannelDto requestDto);

  ResponseChannelDto findChannel(UUID channelId, UUID userId);

  ChannelType getChannelType(UUID id);

  UUID getChannelId(String name);

  List<ResponseChannelDto> findAllChannel(UUID userId);

  List<ResponseChannelDto> findAllPrivateChannel(UUID userId);

  List<ResponseChannelDto> accessiblePrivateChannel(UUID userId);

  ResponseChannelDto requestChannelInfo(Channel channel);

  void includePrivateChannel(String channelName, String username, UUID userId);

  void excludePrivateChannel(String channelName, String username);

  boolean deleteChannel(UUID id);

  void deleteAllChannel(String name);

  boolean isPresentChannel(UUID id);

  boolean isCreatePrivateChannel(String name);

  boolean findChannelCreator(UUID id, String username);

  UUID channelNameToId(String name);

  String channelIdToName(UUID id);
}
