package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileChannelRepository implements ChannelRepository {
    private final Map<String, Channel> channelNameMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    /// Singleton
    private FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Channel.class.getSimpleName());
        if(Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(channel -> {
                        channelNameMap.put(channel.getName(), channel);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static class Holder {
        private static final FileChannelRepository INSTANCE = new FileChannelRepository();
    }
    public static FileChannelRepository getInstance() {
        return Holder.INSTANCE;
    }


    /// interface
    @Override
    public boolean save(Channel channel) {
        // 입력값: 현재 채널, 변경할 이름
        // TODO: 채널명 변경, 기존 키 삭제 및 추가

        Path path = resolvePath(channel.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();
            channelNameMap.getOrDefault(channel.getName(),
                    channelNameMap.put(channel.getName(), channel));

            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] " + e);
        }
        return false;
    }

    @Override
    public boolean save(String oldName, String newName) {
        Channel channel = channelNameMap.get(oldName);
        Path path = resolvePath(channelNameMap.get(oldName).getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(channel);
            oos.flush();

            channelNameMap.put(newName, channel);
            channelNameMap.remove(channel.getName());
            channel.channelUpdater(newName);

            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] " + e);
            return false;
        }
    }

    @Override
    public String readChannel(String name) {
        try {
            return channelNameMap.get(channelNameMap.get(name).getName()).toString();
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

        Path path = resolvePath(id);
        try {
            Files.delete(path);
            channelNameMap.remove(name);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    ///
    public boolean check(String name) {
        return channelNameMap.getOrDefault(channelNameMap.get(name).getName(), null) == null;
    }
}
