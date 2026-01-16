package com.sprint.mission.discodeit.app.router;

import java.util.Scanner;

public class Router {
    public static void route(Scanner sc, int n) {
        int m;
        switch (n) {
            /// TODO: 중복 출력 부분 별도 로직으로 분리하기
            case 1:

                m = sc.nextInt();
                sc.nextLine();
                RouteUserService.userService(sc, m);
                break;
            case 2:
                System.out.println("채널 관련 서비스입니다.");
                System.out.println("어떤 서비스를 원하시나요?");
                System.out.println("=====================");
                System.out.println("1. Channel Create");
                System.out.println("2. Channel Update");
                System.out.println("3. Channel Read");
                System.out.println("4. Channel Delete");
                m = sc.nextInt();
                sc.nextLine();
                RouteChannelService.channelService(sc, m);
                break;
            case 3:
                System.out.println("메시지 관련 서비스입니다.");
                System.out.println("어떤 서비스를 원하시나요?");
                System.out.println("=====================");
                System.out.println("1. Message Create");
                System.out.println("2. Message Update");
                System.out.println("3. Message Read");
                System.out.println("4. Message Delete");
                m = sc.nextInt();
                sc.nextLine();
                RouteMessageService.messageService(sc, m);
                break;
            default:
                return;
        }
    }
}
