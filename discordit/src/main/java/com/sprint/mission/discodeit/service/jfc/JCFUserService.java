package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final List<User> users = new ArrayList<>();
//    private final Map<UUID, User> usersMap = new HashMap<>();
//    private final Map<UUID, String> usersName = new HashMap<>();

    /// Create
    @Override
    public void createUser(Scanner sc) {
        String name;
        String pw;
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        name = sc.nextLine();

        /// forEach 안에서 return은 forEach문을 종료하는 것으로 끝남...
        if(users.stream().anyMatch(e -> Objects.equals(e.getName(), name))) {
            System.out.println("이미 존재하는 사용자명입니다.");
            return;
        }

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        pw = sc.nextLine();
        User user = new User(name, pw);
        users.add(user);

//        usersName.put(user.getId(), name);
//        usersMap.put(user.getId(), user);
    }

    ///  테스트 코드 작성용
    public void createUser(String name, String pw) {
        users.add(new User(name, pw));
    }

    /// Update
    @Override
    public void updateUser(Scanner sc) {
        System.out.println("====================");
        System.out.println("1. 이름 변경");
        System.out.println("2. 비밀번호 변경");
        System.out.println("3. 이메일 변경");
        System.out.println("4. 전화번호 변경");
        System.out.println("====================");
        int n = sc.nextInt();
        sc.nextLine();

        switch(n){
            case 1:
                updateUserName(sc);
                break;
            case 2:
                updateUserPw(sc);
                break;
            case 3:
                updateUserEmail(sc);
                break;
            case 4:
                updateUserPhonenumber(sc);
                break;
            default:
                break;
        }
    }
    private void updateUserName(Scanner sc){
        User c = check(sc, "변경");

        if(check(sc, "변경") == null) {
            System.out.println("맞는 계정이 없습니다.");
            return;
        }

        System.out.println("변경하고자 하는 이름을 입력해주세요");
        String rename = sc.nextLine().trim();
//        usersMap.keySet();

        System.out.println(c.getName() + "에서 " + rename +"으로 변경되었습니다.");
        c.setEmail(rename);
    }
    private void updateUserPw(Scanner sc){
        User c = check(sc, "변경");
        if (c == null) {
            System.out.println("맞는 계정이 없습니다.");
            return;
        }
        System.out.println("변경하고자 하는 비밀번호를 입력해주세요");
        String repw = sc.nextLine();
        System.out.println("상공");
        c.setPw(repw);
    }
    private void updateUserEmail(Scanner sc){
        User c = check(sc, "변경");
        if (c == null) {
            System.out.println("맞는 계정이 없습니다.");
            return;
        }
        System.out.println("변경하고자 하는 이메일을 입력해주세요");
        String remail = sc.nextLine();

        System.out.println(c.getEmail() + "에서 " + remail +"으로 변경되었습니다.");
        c.setEmail(remail);
    }
    private void updateUserPhonenumber(Scanner sc){
        User c = check(sc, "변경");
        if (c == null) {
            System.out.println("맞는 계정이 없습니다.");
            return;
        }

        System.out.println("변경하고자 하는 연락처를 입력해주세요");
        String repn = sc.nextLine();
        System.out.println(c.getPhonenumber() + "에서 " + repn +"으로 변경되었습니다.");
        c.setPhonenumber(repn);
    }

    /// Read
    public User getUserId(UUID id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void getUserName(Scanner sc) {
        System.out.println("조회하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();

        for (User u : users) {
            if (u.getName().equals(name)) {
                System.out.println("====================");
                System.out.println("사용자ID : " + u.getId());
                System.out.println("사용자명 : " + u.getName());
                System.out.println("이메일 : " + u.getEmail());
                System.out.println("전화번호 : " + u.getPhonenumber());
                System.out.println("생성일 : " + u.getCreateAt());
                System.out.println("수정일 : " + u.getUpdateAt());
                System.out.println("====================");
                return;
            }
        }
        System.out.println("조회하고자 하는 사용자가 없습니다.");
    }
    public User getUserName(String name) {
        return users.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
    public String getUserName(UUID id) {
        User user = users.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        if(user == null) return null;
        return user.getName();
    }
    @Override
    public void getAllUserName() {
        users.forEach(u -> {
            System.out.println("====================");
            System.out.println("사용자ID : " + u.getId());
            System.out.println("사용자명 : " + u.getName());
            System.out.println("이메일 : " + u.getEmail());
            System.out.println("전화번호 : " + u.getPhonenumber());
            System.out.println("생성일 : " + u.getCreateAt());
            System.out.println("수정일 : " + u.getUpdateAt());
            System.out.println("====================");
        });
    }

    /// Delete
    @Override
    public void deleteUser(Scanner sc) {
        int n;
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");

        n = sc.nextInt();
        sc.nextLine();
        if(n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        };

        User target = check(sc, "삭제");

        if (target == null) {
            System.out.println("일치하는 계정을 찾을 수 없습니다.");
            return;
        }
        users.remove(target);
        System.out.println("계정이 삭제되었습니다.");
    }

    public User check(Scanner sc, String work) {
        System.out.println(work+"하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();

        return users.stream().filter(u -> u.getName().equals(name) && u.getPw().equals(pw)).findFirst().orElse(null);
    }
}
