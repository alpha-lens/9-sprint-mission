package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    boolean create(String text, String sendeeChannelName, String senderUserName);

    boolean update(UUID messageId, String content);

    List<String> findAllForSender(UUID userId);

    List<String> findAllInChannel(String name);

    boolean delete(UUID userId, UUID messageId);

    void delete(UUID channelId);

    String lastMessageTime(String channelName);
}
