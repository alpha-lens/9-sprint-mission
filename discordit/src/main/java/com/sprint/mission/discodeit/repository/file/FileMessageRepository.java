package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    FileChannelRepository channelRepository = FileChannelRepository.getInstance();
    FileUserRepository userRepository = FileUserRepository.getInstance();

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

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
                                .computeIfAbsent(message.getSendChannelId(), id -> new ArrayList<>())
                                .add(message);

                        userIdMessageMap
                                .computeIfAbsent(message.getSenderUserId(), id -> new ArrayList<>())
                                .add(message);

                        messageIdMap.put(message.getId(), message);
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private static final FileMessageRepository INSTANCE = new FileMessageRepository();
    }

    public static FileMessageRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean createMessage(String content, String sendeeChannelName, String senderUser) {
        UUID channelId = channelRepository.readChannelId(sendeeChannelName);
        UUID userId = userRepository.userNameToId(senderUser);

        Message message = new Message(channelId, userId, content);
        channelIdMessageMap.computeIfAbsent(channelId, m -> new ArrayList<>()).add(message);
        userIdMessageMap.computeIfAbsent(userId, m -> new ArrayList<>()).add(message);
        Path path = resolvePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<String> getInChannelMessage(String channelName) {
        List<String> result = new ArrayList<>();
        try{
            channelIdMessageMap.get(channelRepository.channelNameToId(channelName))
                    .stream().sorted(Comparator.comparing(Message::getCreateAt))
                    .forEach(message -> result.add(formatMessage(message)));
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public List<String> getMessageForSender(String senderName) {
        List<String> result = new ArrayList<>();
        try{
            userIdMessageMap.get(userRepository.userNameToId(senderName))
                    .forEach(message -> result.add(formatMessage(message)));
        } catch (Exception e) {
            return List.of();
        }
        return result;
    }

    @Override
    public boolean updateMessage(UUID id, String content) {
        Path path = resolvePath(id);
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(messageIdMap.get(id));
            messageIdMap.get(id).updateMessage(content);
            System.out.println("성공");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean deleteMessage(String userName, UUID id) {
        UUID userId = userRepository.userNameToId(userName);
        Message message = userIdMessageMap.get(userId).stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        UUID channelId = message.getSendChannelId();

        Path path = resolvePath(id);
        try {
            Files.delete(path);
            userIdMessageMap.get(userId).remove(message);
            channelIdMessageMap.get(channelId).remove(message);
            messageIdMap.remove(message.getId());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean check(String userName, UUID id) {
        Object result = userIdMessageMap.get(userRepository.userNameToId(userName)).stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        return result == null;
    }


    /// formatting method
    private String formatMessage(Message message) {
        return "====================\n" +
              "메시지ID: " + message.getId().toString() + "\n"
            + "채널명: " + channelRepository.channelIdToName(message.getSendChannelId()) + "\n"
            + "보낸이: " + userRepository.userIdToName(message.getSenderUserId()) + "\n"
            + "생성일: " + sdf.format(message.getCreateAt()) + "\n"
            + "수정일: " + sdf.format(message.getUpdateAt()) + "\n"
            + "내용: " + message.getContent() + "\n";
    }
}
