package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Scanner;
import java.util.UUID;

public interface ChannelService {
    void createServer(Scanner sc);
    void updateServer(Scanner sc);
    void readAllChannel();
    void deleteChannel(Scanner sc);

    /// check
    void isChannelName(Scanner sc);
    Channel isChannelName(String name);
    String isChannelName(UUID id);
    Channel check(String name);
    Channel check(UUID id);
}
