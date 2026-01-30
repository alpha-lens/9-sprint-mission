package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final UserState userState;
    private final Scanner scanner;
    private final FileUserRepository userRepository;
    private final FileChannelRepository channelRepository;
    private final FileMessageRepository messageRepository;

    @Override
    public void createMessage() {
        String senderUserName = userState.getUserName();
        System.out.println("현재 사용자 : " + senderUserName);
        System.out.println("어디로 보내는 메시지인가요?");
        String sendeeChannelName = scanner.nextLine();

        if(!channelRepository.isPresentChannel(sendeeChannelName)) {
            System.err.println("존재하지 않는 채널입니다.");
            return;
        }

        UUID channelId = channelRepository.readChannelId(sendeeChannelName);
        UUID userId = userRepository.userNameToId(senderUserName);

        while(true) {
            System.out.println("현재 메시지를 보낼 채널은 " + sendeeChannelName + "입니다.");
            System.out.println("현재 메시지를 보낼 사람은 " + senderUserName + "입니다.");
            System.out.println("무어라 보내고 싶으신가요?");
            String text = scanner.nextLine();

            System.out.println("현재 채널에 '" + text + "'라고 보내려 합니다.");
            System.out.println("보내시려면 1을, 아니라면 0을 입력해주세요");
            String n = scanner.nextLine();

            if (Objects.equals(n, "0")) {
                System.out.println("처음으로 돌아갑니다.");
                return;
            } else if (Objects.equals(n, "1")) {
                if(messageRepository.createMessage(text, channelId, userId)) {
                    System.out.println("성공.");
                }
                return;
            } else {
                System.err.println("잘못 입력했습니다. 메시지 입력 부분으로 돌아갑니다.");
            }
        }
    }

    @Override
    public void getMessageForSender(UUID userId) {
        List<MessageResponseDto> messageRequestDto = messageRepository.getMessageForSender(userId);
        if (messageRequestDto.isEmpty()) {
            System.out.println("아쉽지만 아무것도 없네요!");
            return;
        }

        System.out.println("당신이 보낸 메시지는 아래와 같습니다.");
        messageRequestDto.forEach(dto -> System.out.println(formattingMessage(dto)));

    }

    @Override
    public void getMessageInChannel(String channelName) {
        UUID channelId = channelRepository.channelNameToId(channelName);
        List<MessageResponseDto> messageRequestDto = messageRepository.getInChannelMessage(channelId);

        if (messageRequestDto.isEmpty()) {
            System.out.println("해당 채널에 보낸 메시지가 없어요.");
            return;
        }

        messageRequestDto.forEach(dto -> System.out.println(formattingMessage(dto)));
        System.out.println("총 메시지 : " + messageRequestDto.size());
    }

    private String formattingMessage(MessageResponseDto dto) {
        String id = "ID: " + dto.id().toString();
        String user = "사용자명: " + userRepository.userIdToName(dto.userId());
        String channel = "채널명: " + channelRepository.channelIdToName(dto.channelId());

        return id + "\n" + user + "\n" + channel + "\n" + "내용: " + dto.content() + "\n" + "생성일: " + dto.createAt() + "\n" + "수정일: " + dto.updateAt();
    }

    @Override
    public void updateMessage(String userName) {
        UUID userId = userRepository.userNameToId(userName);

        getMessageForSender(userId);

        System.out.println("어떤 것을 수정하고 싶나요?");
        String id = scanner.nextLine();

        if (messageRepository.check(userId, UUID.fromString(id))) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String content = scanner.nextLine();

        if(messageRepository.updateMessage(UUID.fromString(id), content)) {
            System.out.println("성공적으로 변경되었습니다.");
        } else {
            System.err.println("실패!");
        }
    }

    @Override
    public void deleteMessage(String userName) {
        UUID userId = userRepository.userNameToId(userName);

        getMessageForSender(userId);
        System.out.println("어떤 메시지를 삭제하고 싶나요? ID로 입력해주세요.");
        String id = scanner.nextLine();

        if (messageRepository.check(userId, UUID.fromString(id))) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("해당 메시지를 삭제합니까? (Y or any Key)");
        String isDelete = scanner.nextLine();

        if (isDelete.equalsIgnoreCase("Y")) {
            if(messageRepository.deleteMessage(userId, UUID.fromString(id))) {
                System.out.println("성공!");
            } else System.out.println("실패!");
        } else System.out.println("초기로 돌아갑니다");
    }
}
