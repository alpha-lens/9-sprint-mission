package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FileMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    FileChannelRepository channelRepository = FileChannelRepository.getInstance();
    FileUserRepository userRepository = FileUserRepository.getInstance();

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    private FileMessageRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY).filter(path-> path.toString().endsWith(EXTENSION))
                    .map(path->{
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(message -> {
                        channelIdMessageMap
                                .computeIfAbsent(message.getSendChannel(), id -> new ArrayList<>())
                                .add(message);

                        userIdMessageMap
                                .computeIfAbsent(message.getSendUserId(), id -> new ArrayList<>())
                                .add(message);
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private static final FileMessageRepository INSTANCE = new FileMessageRepository();
    }

    public static FileMessageRepository getInstance() {
        return FileMessageRepository.Holder.INSTANCE;
    }

    public boolean createMessage(String content, String sendeeChannelName, String senderUser) {
        channelRepository.readChannel(sendeeChannelName)
        Message message = new Message(channel.getId(), user.getId(), text);
        channelIdMessageMap.computeIfAbsent(channel.getId(), m -> new ArrayList<>()).add(message);
        userIdMessageMap.computeIfAbsent(user.getId(), m -> new ArrayList<>()).add(message);
        Path path = resolvePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean readInChannelMessage() {}

    public boolean readForSenderMessage() {}

    public boolean updateMessage() {}

    public boolean deleteMessage() {}

    public boolean check(UUID id) {}

    public boolean check(String name) {}
}
