package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.app.JavaApplication;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;

import java.util.Scanner;

public class RouteMessageService {
    static void messageService(int routeCURD) {
        /// Singleton Instance
//        JCFUserService userService = JCFUserService.getInstance();
//        JCFChannelService channelService = JCFChannelService.getInstance();
//        JCFMessageService messageService = JCFMessageService.getInstance();
        FileUserRepository userRepository = FileUserRepository.getInstance();
        FileChannelService channelService = FileChannelService.getInstance();
        FileMessageService messageService = FileMessageService.getInstance();

        int m;
        String senderUserName;
        String nowUser;
        String sendeeChannelName;
        Channel nowChannel;
        Scanner sc = JavaApplication.scanner();

        switch (routeCURD) {
            case 1:
                /// create
                messageService.createMessage();
                break;
            case 2:
                /// update
                System.out.println("누가 보낸 메시지인가요?");
                senderUserName = sc.nextLine();
//                nowUser = userService.getUserByName(senderUserName);
                if (userRepository.isPresentUser(senderUserName)) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                messageService.updateMessage(senderUserName);
                break;
            case 3:
                /// read
                System.out.println("현재는 내가 보낸 메시지, 특정 채널 메시지를 조회하는 기능만 있습니다.");
                System.out.println("1. 내가 보낸 메시지 확인하기");
                System.out.println("2. 채널에 있는 메시지 확인하기");
                m = sc.nextInt();
                sc.nextLine();

                if (m == 1) {
                    System.out.println("누가 보낸 메시지인가요?");
                    senderUserName = sc.nextLine();
                    if (userRepository.isPresentUser(senderUserName)) {
                        System.out.println("존재하지 않는 사용자입니다.");
                        return;
                    }
                    messageService.getMessageForSender(senderUserName);
                } else if (m == 2) {
                    System.out.println("어디로 보내는 메시지인가요?");
                    sendeeChannelName = sc.nextLine();
                    nowChannel = channelService.isChannelName(sendeeChannelName);
                    if (nowChannel == null) {
                        System.out.println("존재하지 않는 채널입니다.");
                        return;
                    }
                    messageService.getMessageInChannel(nowChannel.getId());
                }
                break;
            case 4:
                /// delete
                System.out.println("현재는 내가 보낸 메시지를 삭제하는 기능만 지원하고 있습니다.");
                System.out.println("사용자명은 어떻게 되나요?");
                senderUserName = sc.nextLine();
                if (userRepository.isPresentUser(senderUserName)) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                messageService.deleteMessage(senderUserName);
                break;
            default:
                return;
        }
    }
}
