package com.sprint.mission.discodeit.entity_.service.user;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Login {
    public static void login(Scanner sc, List<User> users) {
        System.out.println("로그인 페이지에 오신 것을 환영합니다.");
        System.out.println("사용자 아이디를 입력해주세요.");
        String id = sc.next();
        sc.nextLine();
        System.out.println("사용자 비밀번호를 입력해주세요.");
        String pw = sc.next();
        sc.nextLine();
        boolean isTrue = isLogin(id, pw, users);
    }

    public static boolean isLogin(String id, String pw, List<User> users) {
        AtomicBoolean isTrue = new AtomicBoolean(false);
        users.forEach(e -> {
                if(e.getName().equals(id) && e.getPw().equals(pw)) isTrue.set(true);
            });
        return isTrue.get();
    }
}
