package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class Router {
    private final RouteMessageService routeMessageService;
    private final RouteUserService routeUserService;
    private final RouteChannelService routeChannelService;
    private final Scanner sc;
    private final AuthService authService;
    private final UserState userState;

    private int inputChecker() {
        try{
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("잘못된 입력값입니다.");
        }
        return -1;
    }

    public void route() {
        int subMenu = 0;
        int menu;

        while(true) {
            boolean isLogin = userState.getUsername().isEmpty();
            System.out.println("====================");
            System.out.println("0. 프로그램 종료하기");
            System.out.println(isLogin ? "1. 로그인하기" :
                    "1. 로그아웃하기 (현재 사용자 : " + userState.getUsername() + ")");
            System.out.println("2. 사용자 관련 서비스");
            System.out.println("3. 채널 관련 서비스");
            System.out.println("4. 메시지 관련 서비스");
            System.out.println("====================");

            menu = sc.nextInt();
            sc.nextLine();

            if (menu == 0) System.exit(0);

            switch (menu) {
                case 1:
                    if (isLogin) {
                        authService.login();
                    } else {
                        authService.logout();
                    }
                    break;

                case 2:
                    RoutePrintText.printText("user");
                    subMenu = inputChecker();

                    if(subMenu == -1) continue;

                    routeUserService.userService(subMenu);
                    break;

                case 3:
                    RoutePrintText.printText("channel");
                    subMenu = inputChecker();

                    if(subMenu == -1) continue;

                    routeChannelService.channelService(subMenu);
                    break;

                case 4:
                    RoutePrintText.printText("message");
                    subMenu = inputChecker();

                    if(subMenu == -1) continue;

                    routeMessageService.messageService(subMenu);
                    break;

                default:
                    return;
            }
        }
    }
}
