package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class JCFUserService implements UserService {
    @Override
    public void data() {
        List<User> users = new ArrayList<>();
        users.add(new User("김경한", "pw"));
        users.add(new User("이진용", "pw"));
        users.add(new User("김수라", "pw"));
    }
    /// Create
    @Override
    public void createUser(Scanner sc, List<User> users) {
        String name;
        String pw;
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        name = sc.next();

        users.forEach(user -> {
            if(Objects.equals(user.getName(), name)) {
                System.out.println("이미 존재하는 사용자명입니다.");
                return;
            }
        });

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        pw = sc.next();
        users.add(new User(name, pw));
    }

    /// Update
    @Override
    public void updateUser(Scanner sc, List<User> users) {
        System.out.println("1. 이름 변경");
        System.out.println("2. 비밀번호 변경");
        System.out.println("3. 이메일 변경");
        System.out.println("4. 전화번호 변경");
        int n = sc.nextInt();

        switch(n){
            case 1:
                updateUserName(sc, users);
                break;
            case 2:
                updateUserPw(sc, users);
                break;
            case 3:
                updateUserEmail(sc, users);
                break;
            case 4:
                updateUserPhonenumber(sc, users);
                break;
            default:
                break;
        }
    }
    public void updateUserName(Scanner sc, List<User> users){
        System.out.println("변경하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();
        if(check(users, name, pw) != null) {
            System.out.println("변경하고자 하는 이름을 입력해주세요");
            String rename = sc.nextLine();
            for (User u : users) {
                if (u.getName().equals(name) && u.getPw().equals(pw)) {
                    u.setName(rename);
                    System.out.println(name + "에서 " + rename + "으로 변경되었습니다.");
                    return;
                }
            }
        } else {
            System.out.println("맞는 계정이 없습니다.");
        }
    }
    public void updateUserPw(Scanner sc, List<User> users){
        System.out.println("변경하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();
        if(check(users, name, pw) != null) {
            System.out.println("변경하고자 하는 비밀번호를 입력해주세요");
            String repw = sc.nextLine();
            for (User u : users) {
                if (u.getName().equals(name) && u.getPw().equals(pw)) {
                    u.setPw(repw);
                    System.out.println("성공");
                    return;
                }
            }
        } else {
            System.out.println("맞는 계정이 없습니다.");
        }
    }
    public void updateUserEmail(Scanner sc, List<User> users){
        System.out.println("변경하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();
        if(check(users, name, pw) != null) {
            System.out.println("변경하고자 하는 이메일을 입력해주세요");
            String remail = sc.nextLine();
            for (User u : users) {
                if (u.getName().equals(name) && u.getPw().equals(pw)) {
                    u.setEmail(remail);
                    System.out.println(u.getEmail() + "에서 " + remail + "으로 변경되었습니다.");
                    return;
                }
            }
        } else {
            System.out.println("맞는 계정이 없습니다.");
        }
    }
    public void updateUserPhonenumber(Scanner sc, List<User> users){
        System.out.println("변경하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();
        if(check(users, name, pw) != null) {
            System.out.println("변경하고자 하는 연락처를 입력해주세요");
            String repn = sc.nextLine();
            for (User u : users) {
                if (u.getName().equals(name) && u.getPw().equals(pw)) {
                    u.setPhonenumber(repn);
                    System.out.println(u.getPhonenumber() + "에서 " + repn + "으로 변경되었습니다.");
                    return;
                }
            }
        } else {
            System.out.println("맞는 계정이 없습니다.");
        }
    }

    /// Read
    @Override
    public void getUserName(User user) {
        System.out.println(user.getId() + " " + user.getName());
    }
    @Override
    public void getAllUserName(List<User> users) {
        users.forEach(u -> {
            System.out.println(u.getId() + " : " + u.getName());
        });
    }

    /// Delete
    @Override
    public void deleteUser(Scanner sc, List<User> users) {
        String name;
        String pw;
        int n;
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");

        n = sc.nextInt();
        if(n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        };

        System.out.print("삭제하려는 계정의 이름을 알려주세요: ");
        name = sc.next();
        System.out.print("삭제하려는 계정의 비밀번호를 알려주세요: ");
        pw = sc.next();

        User target = check(users, name, pw);

        if (target != null) {
            users.remove(target);
            System.out.println("계정이 삭제되었습니다.");
        } else {
            System.out.println("일치하는 계정을 찾을 수 없습니다.");
        }
    }

    public User check(List<User> users, String name, String pw) {
        User target = null;
        for (User u : users) {
            if (u.getName().equals(name) && u.getPw().equals(pw)) {
                target = u;   // 리스트 안에 있는 "그 객체"
                return target;
            }
        }
        return target;
    }
}
