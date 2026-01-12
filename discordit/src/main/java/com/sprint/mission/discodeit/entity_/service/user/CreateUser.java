package com.sprint.mission.discodeit.entity_.service.user;

import java.util.Scanner;
import java.util.List;

public class CreateUser {
    public static void createUser(Scanner sc, List<User> user) {
        System.out.println("회원가입에 오신 것을 환영합니다!");
        System.out.println("사용하려는 회원명을 입력해주세요!");
        String userName = sc.nextLine();
        if(!User.duplicateCheck(user, userName)) {
            System.out.println("[Error!] 이미 존재하는 이름입니다.");
            return;
        }
        System.out.println("사용하려는 비밀번호를 입력해주세요!");
        String userPw = sc.nextLine();
        System.out.println("사용하려는 이메일을 입력해주세요!");
        String userEmail = sc.nextLine();
        System.out.println("사용하려는 전화번호를 입력해주세요!");
        String userPhoneNumber = sc.nextLine();
        System.out.println("입력하신 값은 다음과 같습니다.");
        System.out.printf("사용자명 : %s \n", userName);
        System.out.printf("사용자명 : %s \n", userEmail);
        System.out.printf("사용자명 : %s \n", userPhoneNumber);
        System.out.println("이 값이 맞으면 1, 수정하려면 2, 뒤로 가려면 0을 눌러주세요.");
        int i = sc.nextInt();
        switch(i){
            case 1:
                user.add(new User(userName, userPw, userEmail, userPhoneNumber));
                System.out.println("잘 입력됐습니다!");
            case 2:
                createUser(sc, user);
                break;
            default:
                break;
        }
    }
}
