package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageDto;
import com.sprint.mission.discodeit.dto.apiresponse.ResponseMessage;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    UUID create(CreateMessageDto requestDto);

    ResponseMessage update(UUID userId, UUID messageId, String content);

    List<String> findAllForSender(UUID userId);

    List<String> findAllInChannel(UUID channelId);

    boolean delete(UUID userId, UUID messageId);

    void deleteAll(UUID channelId);

    String lastMessageTime(UUID channelId);
}
