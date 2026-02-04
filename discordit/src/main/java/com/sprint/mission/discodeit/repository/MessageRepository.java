package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    UUID create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList);

    List<MessageRequestDto> findAllInChannel(UUID userId);

    List<MessageRequestDto> findAllForSender(UUID userId);

    boolean updateMessage(UUID id, String content);

    UUID delete(UUID userId, UUID id);

    List<List<UUID>> deleteAll(UUID channelId);
}
