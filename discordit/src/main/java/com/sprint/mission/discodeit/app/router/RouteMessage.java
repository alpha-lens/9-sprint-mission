package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RouteMessage {
    private final BasicMessageService messageService;
    private final Scanner scanner;
    private final IsLogin isLogin;
    private final UserState userState;
    private final ChannelService channelService;
    private final BasicReadStatusService readStatusService;

    public void route(int routeCRUD) {
        if(!isLogin.check("message", routeCRUD)) {
            System.err.println("해당 기능은 로그인한 후 이용 가능합니다.");
            return;
        }

        switch (routeCRUD) {
            case 1:
                /// create
                create();
                break;
            case 2:
                /// update
                messageService.findAllForSender(userState.getUserId());
                update();
                break;
            case 3:
                /// read
                read();
                break;
            case 4:
                /// delete
                delete();
                break;
            default:
                return;
        }
    }

    private void create() {
        String senderUserName = userState.getUserName();
        System.out.println("현재 사용자 : " + senderUserName);
        System.out.println("어디로 보내는 메시지인가요?");
        String sendeeChannelName = scanner.nextLine();
        channelService.isPresent(sendeeChannelName);

        while(true) {
            System.out.println("현재 메시지를 보낼 채널은 " + sendeeChannelName + "입니다.");
            System.out.println("현재 메시지를 보낼 사람은 " + senderUserName + "입니다.");
            System.out.println("무어라 보내고 싶으신가요?");
            String text = scanner.nextLine();

            List<BinaryContentDto> attatchmentList = attatchment();

            System.out.println("현재 채널에 '" + text + "'라고 보내려 합니다.");
            System.out.println("보내시려면 1을, 아니라면 0을 입력해주세요");
            String n = scanner.nextLine();

            if (Objects.equals(n, "0")) {
                System.out.println("처음으로 돌아갑니다.");
                return;
            } else if (Objects.equals(n, "1")) {
                if(messageService.create(text, sendeeChannelName, senderUserName, attatchmentList)) {
                    System.out.println("성공.");
                } else {
                    System.err.println("알 수 없는 오류로 인해 실패했습니다.");
                }
                return;
            } else {
                System.err.println("잘못 입력했습니다. 메시지 입력 부분으로 돌아갑니다.");
            }
        }
    }

    private List<BinaryContentDto> attatchment() {
        System.out.println("추가할 파일을 입력해주세요.");
        System.out.println("마무리됐을 경우, 그냥 엔터를 입력해주세요.");
        List<BinaryContentDto> result = new ArrayList<>();
        while(true) {
            String filename = scanner.nextLine();
            if(filename.trim().isEmpty()) return result;
            if(!filename.matches("^.+\\.(png|jpg)$")) {
                System.err.println("잘못된 확장자 형식입니다. jpg, png만 지원합니다.");
                continue;
            }

            result.add(new BinaryContentDto(AttachmentType.MESSAGE, null, filename));
        }
    }

    private void update() {
        System.out.println("어떤 것을 수정하고 싶나요?");
        String messageId = scanner.nextLine();
        UUID parseUUID;

        try{
            parseUUID = UUID.fromString(messageId);
        } catch (Exception e) {
            System.err.println("잘못된 입력값입니다 : " + e);
            return;
        }

        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String content = scanner.nextLine();

        try {
            if (messageService.update(parseUUID, content)) {
                System.out.println("성공적으로 변경되었습니다.");
            } else {
                System.err.println("알 수 없는 오류로 인해 변경하지 못했습니다.");
            }
        } catch (NotFound e) {
            System.err.println(e);
        }
    }

    private void read() {
        System.out.println("현재는 내가 보낸 메시지, 특정 채널 메시지를 조회하는 기능만 있습니다.");
        System.out.println("1. 내가 보낸 메시지 확인하기");
        System.out.println("2. 채널에 있는 메시지 확인하기");
        int m = scanner.nextInt();
        scanner.nextLine();

        List<String> requestDto;

        if (m == 1) {
            requestDto = messageService.findAllForSender(userState.getUserId());
        } else if (m == 2) {
            System.out.println("어디로 보낸 메시지인가요?");
            String sendeeChannelName = scanner.nextLine();
            if (!channelService.isPresent(sendeeChannelName)) {
                System.out.println("존재하지 않는 채널입니다.");
                return;
            }
            readStatusService.update(userState.getUserId(), sendeeChannelName);
            requestDto = messageService.findAllInChannel(sendeeChannelName);
        } else return;

        showMessage(requestDto);
    }

    private void showMessage(List<String> requestDto) {
        if(requestDto.isEmpty()) {
            System.out.println("아무것도 없네요!");
            return;
        }

        requestDto.forEach(System.out::println);
        System.out.println("총 메세지 개수 : " + requestDto.size());
    }

    private void delete() {
        UUID userId = userState.getUserId();

        System.out.println("현재는 내가 보낸 메시지를 삭제하는 기능만 지원하고 있습니다.");
        System.out.println("당신이 보낸 메시지는 다음과 같습니다.");
        messageService.findAllForSender(userId);

        System.out.println("어떤 메시지를 삭제하고 싶나요? ID로 입력해주세요.");
        String id = scanner.nextLine();
        UUID parseId;

        try{
            parseId = UUID.fromString(id);
        } catch (Exception e) {
            System.err.println("잘못된 입력값입니다. : " + e);
            return;
        }

        if (!messageService.isPresent(userId, parseId)) {
            System.err.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("해당 메시지를 삭제합니까? (Y or any Key)");
        String isDelete = scanner.nextLine();

        if (isDelete.equalsIgnoreCase("Y")) {
            if(messageService.delete(userId, UUID.fromString(id))) {
                System.out.println("성공!");
            } else System.err.println("실패!");
        } else System.out.println("초기로 돌아갑니다");
    }
}
