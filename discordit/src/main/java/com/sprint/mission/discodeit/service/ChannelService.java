package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Scanner;
import java.util.UUID;

public interface ChannelService {
    void createServer(Scanner sc);

    void updateServer(Scanner sc);

    void readChannel(Scanner sc);

    Channel readChannel(String name);

    String readChannel(UUID id);

    void readAllChannel();

    void deleteChannel(Scanner sc);

    Channel check(String name);

    Channel check(UUID id);
}
