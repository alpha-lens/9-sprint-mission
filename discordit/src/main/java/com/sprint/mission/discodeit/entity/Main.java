package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.service.channel.Server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static boolean duplicateCheck(List<Server> list, String name) {
        int temp = list.stream().filter(e -> e.getName().equals(name)).toArray().length;

        if(temp == 0) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        boolean run = true;
        Scanner sc = new Scanner(System.in);
        List<Server> server = new ArrayList<Server>();
        SimpleDateFormat formattingDate = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        /// 채널 생성 잘 되는 것 확인함 - 20260109 1605
        /// formatting 부분까지도 추가해서 수정함.
        server.add(new Server("코드잇 스프린트: 스프링 백엔드 9기"));
        server.add(new Server("스프린트 커뮤니티"));

        /// TODO: 채널 생성 로직 작성
        try {
            Thread.sleep(2000);
            System.out.println("=== 잠시 대기해주세요! ===");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(run) {
            System.out.println("=====================");
            System.out.println("| 0. 종료하기");
            System.out.println("| 1. 서버 생성하기");
            System.out.println("| 2. 서버 조회하기");
            int menu = sc.nextInt();
            sc.nextLine();

            switch (menu) {
                case 0:
                    System.out.println("See you later");
                    run = false;
                    break;
                case 1:
                    System.out.println("서버 생성에 오신 것을 환영합니다!");
                    System.out.println("사용하려는 서버명을 입력해주세요!");
                    String serverName = sc.nextLine();
//                    System.out.println(server.contains(serverName));
                    if(duplicateCheck(server, serverName)) {
                        server.add(new Server(serverName));
                        System.out.println("잘 들어갔어요!");
                    }
                    else System.out.println("error!");
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
