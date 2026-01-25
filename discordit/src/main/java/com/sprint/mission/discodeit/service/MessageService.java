package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public interface MessageService {

    void createMessage();

    void updateMessage(String channelName);

    void getMessageForSender(String senderName);

    void getMessageInChannel(String name);

    void deleteMessage(String userName);
}
