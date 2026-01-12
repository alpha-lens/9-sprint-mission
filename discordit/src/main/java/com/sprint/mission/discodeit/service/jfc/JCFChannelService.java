package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    public void data() {
        List<Channel> channel = new ArrayList<>();
        channel.add(new Channel("스프린트"));
        channel.add(new Channel("커뮤니티"));
    }
}
