package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.app.router.Router;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.Scanner;

public class JavaApplication {
    private static class sc {
        private static final Scanner sc = new Scanner(System.in);
    }

    public static Scanner scanner() {
        return sc.sc;
    }

    public static void main(String[] args) {
        Scanner sc = scanner();
        System.out.println("프로그램을 실행합니다.");
        int menu;


        while (true) {
            System.out.println("====================");
            System.out.println("0. 프로그램 종료하기");
            System.out.println("1. 사용자 관련 서비스");
            System.out.println("2. 채널 관련 서비스");
            System.out.println("3. 메시지 관련 서비스");
            System.out.println("====================");

            menu = sc.nextInt();
            sc.nextLine();

            if (menu == 0) return;

            Router.route(menu);
        }
    }
}
