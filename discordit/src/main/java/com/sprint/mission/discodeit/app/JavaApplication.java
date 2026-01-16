package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.app.router.Router;

import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("프로그램을 실행합니다.");
        boolean run = true;
        int menu;
        Scanner sc = new Scanner(System.in);

        /// FIXME: 여기도 수정할 수 있지 않을까?
        while (run) {
            System.out.println("==========");
            System.out.println("0. 프로그램 종료하기");
            System.out.println("1. 사용자 관련 서비스");
            System.out.println("2. 채널 관련 서비스");
            System.out.println("3. 메시지 관련 서비스");
            System.out.println("==========");
            menu = sc.nextInt();
            sc.nextLine();

            if (menu == 0) return;

            Router.route(sc, menu);
        }
    }
}
