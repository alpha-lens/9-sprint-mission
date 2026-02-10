package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record CreateChannelDto(
    String name, String createUserName, UUID createUserId, ChannelType type
) {
    public Channel toEntity() {
        return new Channel(name, createUserName, createUserId, type);
    }
}
