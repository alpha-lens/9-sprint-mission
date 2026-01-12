package com.sprint.mission.discodeit.entity_;

import com.sprint.mission.discodeit.entity_.service.user.CreateUser;
import com.sprint.mission.discodeit.entity_.service.user.User;

import java.util.List;
import java.util.Scanner;
import static com.sprint.mission.discodeit.entity_.service.user.Login.login;

public class Route {
    public static void route(Scanner sc, int n, List<User> user) {
        System.out.println("=====================");
        System.out.println("| 0. 종료하기");

        switch (n){
            ///  비로그인
            case 0:
                System.out.println("| 1. 로그인");
                System.out.println("| 2. 회원가입");
                int menu = sc.nextInt();
                sc.nextLine();
                switch(menu) {
                    case 1:
                        login(sc, user);
                        break;
                    case 2:
                        CreateUser.createUser(sc, user);
                }
                break;
            ///  로그인 후
            case 1:
                System.out.println("| 0. 로그아웃");
                System.out.println("| 1. 서버 조회하기");
                System.out.println("| 2. 서버 접속하기");
                System.out.println("| 3. 서버 생성하기");
                break;
            ///  서버 접속했을 때
            case 2:
                System.out.println("| 0. 뒤로가기");
                System.out.println("| 1. 현재 서버 확인하기");
                System.out.println("| 2. 현재 서버 수정하기");
                System.out.println("| 3. 채널 확인하기");
                System.out.println("| 4. 채널 접속하기");
                System.out.println("| 5. 채널 생성하기");
                break;
            /// 채널에 들어갔을 때
            case 3:
                System.out.println("| 0. 뒤로가기");
                System.out.println("| 1. 현재 채널 확인하기");
                System.out.println("| 2. 현재 채널 수정하기");
                System.out.println("| 3. 메시지 확인하기");
                System.out.println("| 4. 메시지 수정하기");
                System.out.println("| 5. 메시지 전송하기");
                System.out.println("| 6. [Wanning!] 채널 삭제하기");
                System.out.println("| 7. [Wanning!] 메시지 삭제하기");
        }
    }
}
