package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.apiresponse.ResponseMessage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    UUID create(String content, UUID channelId, UUID userId, List<UUID> attachmentIdList);

    List<MessageResponseDto> findAllInChannel(UUID userId);

    Instant getLastMessageInChannel(UUID channelId);

    List<MessageResponseDto> findAllForSender(UUID userId);

    ResponseMessage updateMessage(UUID id, String content);

    UUID delete(UUID userId, UUID id);

    List<List<UUID>> deleteAll(UUID channelId);

    boolean isPresentMessage(UUID userId, UUID id);
}
