package com.sprint.mission.discodeit.repository.file;


import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class FileMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
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

    @Override
    public boolean createMessage(String content, UUID channelId, UUID userId) {
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
    public List<MessageResponseDto> getInChannelMessage(UUID channelId) {
        List<MessageResponseDto> result = new ArrayList<>();
        try{
            channelIdMessageMap.get(channelId)
                    .stream().sorted(Comparator.comparing(Message::getCreateAt))
                    .forEach(message -> {
                        result.add(new MessageResponseDto(
                                message.getId(), message.getChannelId(), message.getUserId(), FORMATTER.format(message.getCreateAt()), FORMATTER.format(message.getUpdateAt()), message.getContent()
                        ));
                    });
            return result;
        } catch (Exception e) {
            return List.of();
        }
    }

    public Instant getLastMessageInChannel(UUID channelId) {
        try {
            return channelIdMessageMap.get(channelId)
                    .stream().max(Comparator.comparing(Message::getCreateAt)).orElse(null).getCreateAt();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<MessageResponseDto> getMessageForSender(UUID userId) {
        List<MessageResponseDto> result = new ArrayList<>();
        try{
            userIdMessageMap.get(userId)
                    .stream().sorted(Comparator.comparing(Message::getCreateAt))
                    .forEach(message -> {
                        result.add(new MessageResponseDto(
                                message.getId(), message.getChannelId(), message.getUserId(), FORMATTER.format(message.getCreateAt()), FORMATTER.format(message.getUpdateAt()), message.getContent()
                        ));
                    });
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
    public boolean deleteMessage(UUID userId, UUID id) {
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

    public boolean check(UUID userId, UUID id) {
        Object result = userIdMessageMap.get(userId).stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        return result == null;
    }
}
