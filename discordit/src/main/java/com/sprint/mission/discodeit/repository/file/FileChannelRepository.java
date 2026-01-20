package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.Scanner;
import java.util.UUID;

public class FileChannelRepository implements ChannelService {
    @Override
    public void createChannel(Scanner sc) {

    }

    @Override
    public void updateChannel(Scanner sc) {

    }

    @Override
    public void readAllChannel() {

    }

    @Override
    public void deleteChannel(Scanner sc) {

    }

    @Override
    public void isChannelName(Scanner sc) {

    }

    @Override
    public Channel isChannelName(String name) {
        return null;
    }

    @Override
    public String isChannelName(UUID id) {
        return "";
    }

    @Override
    public Channel check(String name) {
        return null;
    }

    @Override
    public Channel check(UUID id) {
        return null;
    }
}
