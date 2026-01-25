package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.app.JavaApplication;
import com.sprint.mission.discodeit.service.file.FileUserService;
//import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.Scanner;

public class RouteUserService {
    static void userService(int routeCRUD) {
//        JCFUserService userService = JCFUserService.getInstance();
        FileUserService userService = FileUserService.getInstance();
        int menu;
        Scanner sc = JavaApplication.scanner();

        switch (routeCRUD) {
            /// create
            case 1:
                userService.createUser();
                break;

            /// update
            case 2:
                userService.updateUser();
                break;

            /// read
            case 3:
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 사용자만 가져옵니다");
                System.out.println("2 : 전체 사용자를 가져옵니다");

                menu = sc.nextInt();
                sc.nextLine();

                if (menu == 1) userService.getUserName();
                else if (menu == 2) userService.getAllUserName();
                break;

            /// delete
            case 4:
                userService.deleteUser();
                break;

            default:
                return;
        }
    }
}
