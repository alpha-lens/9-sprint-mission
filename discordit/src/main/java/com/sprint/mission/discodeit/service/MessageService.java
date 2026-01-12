package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public interface MessageService {
    void data();
    void createMessage(Scanner sc, List<Message> messages);
    void updateMessage(Scanner sc, List<Message> messages);
    void getMessageName(Message user);
    void getAllMessageName(List<Message> users);
    void deleteMessage(Scanner sc, List<Message> messages);
}
