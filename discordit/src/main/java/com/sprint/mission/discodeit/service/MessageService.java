package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponseDto create(CreateMessageDto requestDto);

    MessageResponseDto update(UUID userId, UUID messageId, String content);

    List<String> findAllForSender(UUID userId);

    List<MessageResponseDto> findAllInChannel(UUID channelId);

    boolean delete(UUID userId, UUID messageId);

    void deleteAll(UUID channelId);

    String lastMessageTime(UUID channelId);
}
