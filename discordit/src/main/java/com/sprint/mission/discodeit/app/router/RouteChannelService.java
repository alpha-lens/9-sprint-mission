package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.service.jfc.JCFChannelService;

import java.util.Scanner;

public class RouteChannelService {
    static void channelService(Scanner sc, int routeCRUD) {
        JCFChannelService channelService = JCFChannelService.getInstance();
        int menu;

        switch (routeCRUD) {

            /// create
            case 1:
                channelService.createServer(sc);
                break;

            /// update
            case 2:
                channelService.updateServer(sc);
                break;

            /// read
            case 3:
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 채널만 가져옵니다");
                System.out.println("2 : 전체 채널을 가져옵니다");

                menu = sc.nextInt();
                sc.nextLine();

                if (menu == 1) channelService.isChannelName(sc);
                else if (menu == 2) channelService.readAllChannel();
                break;

            /// delete
            case 4:
                channelService.deleteChannel(sc);
                break;

            default:
                return;
        }
    }
}
