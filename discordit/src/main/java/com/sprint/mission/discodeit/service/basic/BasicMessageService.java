package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserState userState;
    private final FileUserRepository userRepository;
    private final FileChannelRepository channelRepository;
    private final FileMessageRepository messageRepository;
    private final FileBinaryContentRepository binaryContentRepository;

    public boolean isPresent(UUID userId, UUID messageId) {
        return messageRepository.check(userId, messageId);
    }

    @Override
    public boolean create(String text, String sendeeChannelName, String senderUserName) {
        UUID channelId = channelRepository.readChannelId(sendeeChannelName);
        UUID userId = userRepository.userNameToId(senderUserName);

        /// 사실 2차 검증이라 불필요한거 같긴 한데..
        return channelId != null && userId != null && !messageRepository.create(text, channelId, userId).isEmpty();
    }

    public boolean create(String text, String sendeeChannelName, String senderUserName, List<BinaryContentDto> binaryContentDtos) {
        UUID channelId = channelRepository.readChannelId(sendeeChannelName);
        UUID userId = userRepository.userNameToId(senderUserName);

        String messageIdS = messageRepository.create(text, channelId, userId);

        if(messageIdS.isEmpty()) throw new NotFound("메시지 ID가 없습니다");

        UUID messageId = UUID.fromString(messageIdS);

        binaryContentDtos.forEach(content -> binaryContentRepository.create(AttachmentType.MESSAGE, messageId, content.filename()));

        return true;
    }

    @Override
    public List<String> findAllForSender(UUID userId) {
        return messageRepository.findAllForSender(userId).stream().map(this::formattingMessage).toList();
    }

    @Override
    public List<String> findAllInChannel(String channelName) {
        UUID channelId = channelRepository.channelNameToId(channelName);
        return messageRepository.findAllInChannel(channelId).stream().map(this::formattingMessage).toList();
    }

    private String formattingMessage(MessageResponseDto dto) {
        String id = "ID: " + dto.id().toString();
        String user = "사용자명: " + userRepository.userIdToName(dto.userId());
        String channel = "채널명: " + channelRepository.channelIdToName(dto.channelId());

        return id + "\n"
                + user + "\n"
                + channel + "\n"
                + "내용: " + dto.content() + "\n"
                + "첨부파일: " + binaryContentRepository.find(AttachmentType.MESSAGE, dto.id()) + "\n"
                + "생성일: " + dto.createAt() + "\n"
                + "수정일: " + dto.updateAt();
    }

    @Override
    public boolean update(UUID messageId, String content) {
        UUID userId = userState.getUserId();

        if (messageRepository.check(userId, messageId)) {
            throw new NotFound("해당 ID를 찾지 못했습니다.");
        }

        return messageRepository.updateMessage(messageId, content);
    }

    @Override
    public boolean delete(UUID userId, UUID messageId) {
        return messageRepository.delete(userId, messageId);
    }

    @Override
    public void delete(UUID channelId) {
        messageRepository.delete(channelId);
    }

    @Override
    public String lastMessageTime(String channelName) {
        UUID channelId = channelRepository.channelNameToId(channelName);
        return messageRepository.findAllInChannel(channelId).stream().map(MessageResponseDto::createAt).max(String::compareTo).orElse("없음");
    }
}
