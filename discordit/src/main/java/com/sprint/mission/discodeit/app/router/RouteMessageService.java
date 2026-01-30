package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class RouteMessageService {
    private final FileUserRepository userRepository;
    private final FileChannelRepository channelRepository;
    private final BasicMessageService messageService;
    private final Scanner scanner;
    private final IsLogin isLogin;
    private final UserState userState;

    public void messageService(int routeCRUD) {
        int m;
        String sendeeChannelName;
        if(!isLogin.check("message", routeCRUD)) {
            System.err.println("해당 기능은 로그인한 후 이용 가능합니다.");
            return;
        }

        switch (routeCRUD) {
            case 1:
                /// create
                messageService.createMessage();
                break;
            case 2:
                /// update
                messageService.updateMessage(userState.getUserName());
                break;
            case 3:
                /// read
                System.out.println("현재는 내가 보낸 메시지, 특정 채널 메시지를 조회하는 기능만 있습니다.");
                System.out.println("1. 내가 보낸 메시지 확인하기");
                System.out.println("2. 채널에 있는 메시지 확인하기");
                m = scanner.nextInt();
                scanner.nextLine();

                if (m == 1) {
                    messageService.getMessageForSender(userState.getUserId());
                } else if (m == 2) {
                    System.out.println("어디로 보낸 메시지인가요?");
                    sendeeChannelName = scanner.nextLine();
                    if (channelRepository.isPresentChannel(sendeeChannelName)) {
                        System.out.println("존재하지 않는 채널입니다.");
                        return;
                    }
                    messageService.getMessageInChannel(sendeeChannelName);
                }
                break;
            case 4:
                /// delete
                System.out.println("현재는 내가 보낸 메시지를 삭제하는 기능만 지원하고 있습니다.");
                messageService.deleteMessage(userState.getUserName());
                break;
            default:
                return;
        }
    }
}
