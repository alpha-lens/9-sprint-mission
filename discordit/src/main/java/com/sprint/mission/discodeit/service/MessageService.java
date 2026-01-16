package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public interface MessageService {

    void createMessage(Scanner sc, Channel channel, User user);

    void updateMessage(Scanner sc, User user);

    void getMessageForSender(User sender);

    void getMessageInChannel(UUID channelId, JCFUserService userService);

    void deleteMessage(Scanner sc, User user);
}
