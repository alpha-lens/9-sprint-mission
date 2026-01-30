package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageResponseDto;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    boolean createMessage(String content, UUID channelId, UUID userId);

    List<MessageResponseDto> getInChannelMessage(UUID userId);

    List<MessageResponseDto> getMessageForSender(UUID userId);

    boolean updateMessage(UUID id, String content);

    boolean deleteMessage(UUID userId, UUID id);
}
