package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.UUID;

public record RequestCreateChannelDto(
    String name, String createUsername, UUID createUserId, ChannelType type, UUID participantIds
) {

  public Channel toEntity() {
    return new Channel(name, createUsername, createUserId, type);
  }
}
