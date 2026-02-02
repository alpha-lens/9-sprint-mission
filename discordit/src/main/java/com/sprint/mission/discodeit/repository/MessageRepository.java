package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageResponseDto;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    String create(String content, UUID channelId, UUID userId);

    List<MessageResponseDto> findAllInChannel(UUID userId);

    List<MessageResponseDto> findAllForSender(UUID userId);

    boolean updateMessage(UUID id, String content);

    boolean delete(UUID userId, UUID id);

    void delete(UUID channelId);
}
