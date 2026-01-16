package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.service.jfc.JCFChannelService;

import java.util.Scanner;

public class RouteChannelService {
    static void channelService(Scanner sc, int routeCRUD) {
        JCFChannelService channelService = JCFChannelService.getInstance();
        int menu;
        switch (routeCRUD) {
            case 1:
                /// create
                channelService.createServer(sc);
                break;
            case 2:
                /// update
                channelService.updateServer(sc);
                break;
            case 3:
                /// read
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 채널만 가져옵니다");
                System.out.println("2 : 전체 채널을 가져옵니다");
                menu = sc.nextInt();
                sc.nextLine();

                if (menu == 1) channelService.isChannelName(sc);
                else if (menu == 2) channelService.readAllChannel();
                break;
            case 4:
                /// delete
                channelService.deleteChannel(sc);
                break;
            default:
                return;
        }
    }
}
