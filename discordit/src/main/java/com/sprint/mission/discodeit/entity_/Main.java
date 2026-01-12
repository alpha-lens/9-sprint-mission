package com.sprint.mission.discodeit.entity_;

import com.sprint.mission.discodeit.entity_.service.channel.CreateServer;
import com.sprint.mission.discodeit.entity_.service.channel.Server;
import com.sprint.mission.discodeit.entity_.service.user.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        boolean run = true;
        Scanner sc = new Scanner(System.in);
        List<Server> server = new ArrayList<Server>();
        List<User> user = new ArrayList<User>();
        SimpleDateFormat formattingDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        /// 채널 생성 잘 되는 것 확인함 - 20260109 1605
        /// formatting 부분까지도 추가해서 수정함.
        server.add(new Server("코드잇 스프린트: 스프링 백엔드 9기"));
        server.add(new Server("스프린트 커뮤니티"));
        user.add(new User("이진용", "string", "ddd@naver.com", "010-0000-0000"));
        user.add(new User("김경한", "string", "eee@naver.com", "010-0000-0000"));

        /// TODO: 채널 생성 로직 작성
        try {
            Thread.sleep(2000);
            System.out.println("=== 잠시 대기해주세요! ===");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(run) {
            Route.route(sc, 0, user);
            int menu = sc.nextInt();
            sc.nextLine();

            switch (menu) {
                case 0:
                    System.out.println("See you later");
                    run = false;
                    break;
                case 1:
                    CreateServer.createServer(sc, server);
                    break;
                case 2:
//                    System.out.println("현재는 지원하지 않는 기능입니다: " + menu);
                    server.forEach(e -> {
                        System.out.println(e.getName());
                    });
                    break;
                default:
                    System.out.println("[Error!] 존재하지 않는 메뉴입니다.");
                    break;
            }
        }
    }
}
