package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.Scanner;
import java.util.UUID;

public class FileMessageRepository implements MessageService {
    @Override
    public void createMessage(Scanner sc, Channel channel, User user) {

    }

    @Override
    public void updateMessage(Scanner sc, User user) {

    }

    @Override
    public void getMessageForSender(User sender) {

    }

    @Override
    public void getMessageInChannel(UUID channelId, JCFUserService userService) {

    }

    @Override
    public void deleteMessage(Scanner sc, User user) {

    }
}
