package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Scanner;
import java.util.UUID;

public interface ChannelService {
    void createServer(Scanner sc);
    void updateServer(Scanner sc);
    void readServer(Scanner sc);
    Channel readServer(String name);
    String readServer(UUID id);
    void readAllServer();
    void deleteServer(Scanner sc);
    Channel check(String name);
    Channel check(UUID id);
}
