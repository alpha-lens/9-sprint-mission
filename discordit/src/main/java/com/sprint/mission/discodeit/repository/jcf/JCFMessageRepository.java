package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, Message> messageIdMap = new ConcurrentHashMap<>(128);
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final FileChannelRepository channelRepository;
    private final FileUserRepository userRepository;

    @Override
    public boolean createMessage(String content, String sendeeChannelName, String senderUser) {
        UUID channelId = channelRepository.readChannelId(sendeeChannelName);
        UUID userId = userRepository.userNameToId(senderUser);

        Message message = new Message(channelId, userId, content);
        channelIdMessageMap.computeIfAbsent(channelId, m -> new ArrayList<>()).add(message);
        userIdMessageMap.computeIfAbsent(userId, m -> new ArrayList<>()).add(message);
        return true;
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
        try {
            messageIdMap.get(id).updateMessage(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteMessage(String userName, UUID id) {
        UUID userId = userRepository.userNameToId(userName);
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
