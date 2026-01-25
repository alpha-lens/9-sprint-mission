package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<String, Channel> channelNameMap = new ConcurrentHashMap<>();
    private final Map<UUID, Channel> channelIdMap = new ConcurrentHashMap<>();

    /// Singleton
    private JCFChannelRepository() {}
    private static class Holder {
        private static final JCFChannelRepository INSTANCE = new JCFChannelRepository();
    }
    public static JCFChannelRepository getInstance() {
        return Holder.INSTANCE;
    }


    /// interface
    @Override
    public boolean save(Channel channel) {
        channelNameMap.getOrDefault(channel.getName(),
        channelNameMap.put(channel.getName(), channel));
        return true;
    }

    @Override
    public boolean save(String oldName, String newName) {
        Channel channel = channelNameMap.get(oldName);

        try{
            channelNameMap.put(newName, channel);
            channelNameMap.remove(channel.getName());
            channel.channelUpdater(newName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String readChannel(String name) {
        try {
            return channelNameMap.get(name).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public UUID readChannelId(String name) {
        try {
            return channelNameMap.get(name).getId();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Channel> readAllChannel() {
        if(channelNameMap.isEmpty()) {
            return null;
        }
        return channelNameMap.values().stream().toList();
    }

    @Override
    public boolean deleteChannel(String name) {
        UUID id = channelNameMap.get(name).getId();
        channelNameMap.remove(name);
        channelIdMap.remove(id);
        return true;
    }

    ///
    public boolean isPresentChannel(String name) {
        return channelNameMap.getOrDefault(channelNameMap.get(name).getName(), null) == null;
    }

    public UUID channelNameToId(String name) {
        try {
            return channelNameMap.get(name).getId();
        } catch (Exception e) {
            return null;
        }
    }

    public String channelIdToName(UUID id) {
        try {
            return channelIdMap.get(id).getName();
        } catch (Exception e) {
            return null;
        }
    }
}
