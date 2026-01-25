package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    boolean createMessage(String content, String sendeeChannelName, String senderUser);

    List<String> getInChannelMessage(String channelName);

    List<String> getMessageForSender(String senderName);

    boolean updateMessage(UUID id, String content);

    boolean deleteMessage(String userName, UUID id);
}
