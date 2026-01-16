package com.sprint.mission.discodeit.app.router;

import java.util.Scanner;

public class Router {
    private static int inputChecker(Scanner sc, int routeNumber) {
        try{
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("잘못된 입력값입니다.");
            route(sc, routeNumber);
        }
        return -1;
    }

    public static void route(Scanner sc, int menu) {
        int subMenu = 0;

        switch (menu) {
            case 1:
                RoutePrintText.printText("user");
                subMenu = inputChecker(sc, menu);

                if(subMenu == -1) return;

                RouteUserService.userService(sc, subMenu);
                break;

            case 2:
                RoutePrintText.printText("channel");
                subMenu = inputChecker(sc, menu);

                if(subMenu == -1) return;

                RouteChannelService.channelService(sc, subMenu);
                break;

            case 3:
                RoutePrintText.printText("message");
                subMenu = inputChecker(sc, menu);

                if(subMenu == -1) return;

                RouteMessageService.messageService(sc, subMenu);
                break;

            default:
                return;
        }
    }
}
