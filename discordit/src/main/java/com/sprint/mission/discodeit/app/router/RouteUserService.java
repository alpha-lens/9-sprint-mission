package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.Scanner;

public class RouteUserService {
    static void userService(Scanner sc, int routeCRUD) {
        JCFUserService userService = JCFUserService.getInstance();

        int menu;
        switch (routeCRUD) {
            case 1:
                /// create
                userService.createUser(sc);
                break;
            case 2:
                /// update
                userService.updateUser(sc);
                break;
            case 3:
                /// read
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 사용자만 가져옵니다");
                System.out.println("2 : 전체 사용자를 가져옵니다");
                menu = sc.nextInt();
                sc.nextLine();

                if (menu == 1) userService.getUserName(sc);
                else if (menu == 2) userService.getAllUserName();
                break;
            case 4:
                /// delete
                userService.deleteUser(sc);
                break;
            default:
                return;
        }
    }
}
