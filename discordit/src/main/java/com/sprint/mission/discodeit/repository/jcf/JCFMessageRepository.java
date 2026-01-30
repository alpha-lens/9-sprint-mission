package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초").withZone(ZoneId.of("Asia/Seoul"));

    @Override
    public boolean createMessage(String content, UUID channelId, UUID userId) {
        Message message = new Message(channelId, userId, content);
        channelIdMessageMap.computeIfAbsent(channelId, m -> new ArrayList<>()).add(message);
        userIdMessageMap.computeIfAbsent(userId, m -> new ArrayList<>()).add(message);
        return true;
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
        try {
            messageIdMap.get(id).updateMessage(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteMessage(UUID userId, UUID id) {
        Message message = userIdMessageMap.get(userId).stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        UUID channelId = message.getSendChannelId();
        try {
            userIdMessageMap.get(userId).remove(message);
            channelIdMessageMap.get(channelId).remove(message);
            messageIdMap.remove(message.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean check(UUID userId, UUID id) {
        Object result = userIdMessageMap.get(userId).stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        return result == null;
    }
}
