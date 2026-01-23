package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Scanner;
import java.util.UUID;

public interface ChannelService {
    void createChannel();
    void updateChannel();

    void readChannel();

    void readAllChannel();
    void deleteChannel();

    /// check
//    void isChannelName();
//    Channel isChannelName(String name);
//    String isChannelName(UUID id);
//    Channel check(String name);
//    Channel check(UUID id);
}
