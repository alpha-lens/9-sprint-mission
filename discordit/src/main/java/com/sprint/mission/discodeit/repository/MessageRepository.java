package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageResponseDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    MessageResponseDto create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList);

    List<MessageResponseDto> findAllInChannel(UUID channelId);

    Instant getLastMessageInChannel(UUID channelId);

    List<MessageResponseDto> findAllForSender(UUID userId);

    MessageResponseDto updateMessage(UUID id, String content);

    UUID delete(UUID userId, UUID id);

    List<List<UUID>> deleteAll(UUID channelId);

    boolean isPresentMessage(UUID userId, UUID id);
}
