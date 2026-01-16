package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jfc.JCFChannelService;
import com.sprint.mission.discodeit.service.jfc.JCFMessageService;
import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.Scanner;

public class RouteMessageService {
    static void messageService(Scanner sc, int routeCURD) {
        /// Singleton Instance
        JCFUserService userService = JCFUserService.getInstance();
        JCFChannelService channelService = JCFChannelService.getInstance();
        JCFMessageService messageService = JCFMessageService.getInstance();

        int m;
        String senderUserName;
        User nowUser;
        String sendeeChannelName;
        Channel nowChannel;

        switch (routeCURD) {
            case 1:
                /// create
                System.out.println("누가 보내는 메시지인가요?");
                senderUserName = sc.nextLine();
                try {
                    nowUser = userService.getUserByName(senderUserName);
                } catch (Exception e) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }

                System.out.println("어디로 보내는 메시지인가요?");
                sendeeChannelName = sc.nextLine();
                try {
                    nowChannel = channelService.isChannelName(sendeeChannelName);
                } catch (Exception e) {
                    System.out.println("존재하지 않는 채널입니다.");
                    return;
                }

                messageService.createMessage(sc, nowChannel, nowUser);
                break;
            case 2:
                /// update
                System.out.println("누가 보낸 메시지인가요?");
                senderUserName = sc.nextLine();
                nowUser = userService.getUserByName(senderUserName);
                if (nowUser == null) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                messageService.updateMessage(sc, nowUser);
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
                    nowUser = userService.getUserByName(senderUserName);
                    if (nowUser == null) {
                        System.out.println("존재하지 않는 사용자입니다.");
                        return;
                    }
                    messageService.readMsgForUser(nowUser);
                } else if (m == 2) {
                    System.out.println("어디로 보내는 메시지인가요?");
                    sendeeChannelName = sc.nextLine();
                    nowChannel = channelService.isChannelName(sendeeChannelName);
                    if (nowChannel == null) {
                        System.out.println("존재하지 않는 채널입니다.");
                        return;
                    }
                    messageService.readMsgInChannel(nowChannel.getId(), userService);
                }
                break;
            case 4:
                /// delete
                System.out.println("현재는 내가 보낸 메시지를 삭제하는 기능만 지원하고 있습니다.");
                System.out.println("사용자명은 어떻게 되나요?");
                senderUserName = sc.nextLine();
                nowUser = userService.getUserByName(senderUserName);
                if (nowUser == null) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                messageService.deleteMsg(sc, nowUser);
                break;
            default:
                return;
        }
    }
}
