package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserService implements UserService {
    private final Map<UUID, User> usersMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> usersName = new ConcurrentHashMap<>();
    private JCFUserService() {
    }

    private static class Holder {
        private static final JCFUserService INSTANCE = new JCFUserService();
    }

    public static JCFUserService getInstance() {
        return Holder.INSTANCE;
    }

    /// Create
    @Override
    public void createUser(Scanner sc) {
        String name;
        String pw;
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        name = sc.nextLine();

        if (usersName.containsKey(name)) {
            System.out.println("이미 존재하는 사용자명입니다.");
            return;
        }

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        pw = sc.nextLine();
        User user = new User(name, pw);

        usersName.put(name, user.getId());
        usersMap.put(user.getId(), user);
        System.out.println("사용자 " + name + "님이 추가되었습니다.");
    }

    /// Update
    @Override
    public void updateUser(Scanner sc) {
        int isContinue = 0;
        System.out.println("====================");
        System.out.println("사용자 변경 메뉴입니다.");
        User checkUpdateUser = check(sc, "변경");
        if(checkUpdateUser == null) {
            System.out.println("일치하는 사용자가 없습니다.");
            return;
        }

        System.out.println("현재 사용자 변경은 다음과 같은 순서로 입렵받게 됩니다.");
        System.out.println("1. 사용자 이름 변경");
        System.out.println("2. 사용자 비밀번호 변경");
        System.out.println("3. 사용자 이메일 변경");
        System.out.println("4. 사용자 전화번호 변경");
        System.out.println("진행하시려면 1, 아니라면 아무 키나 입력해주세요.");
        String input = sc.nextLine();
        try{
            isContinue = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            System.out.println("잘못 입력하셨습니다.");
            updateUser(sc);
        }

        switch(isContinue) {
            case 1:
                updateUserInfo(sc, checkUpdateUser);
            default:
                System.out.println("이전 메뉴로 돌아갑니다.");
                return;
        }
    }

    /// 사실 분리할 필요가 없긴 한데, 너무 길어져서 분리함
    /// FIXME: 더 좋은 리팩터링 방법이 필요
    private void updateUserInfo(Scanner sc, User checkUpdateUser) {
        String reName;
        String rePassword;
        String reMail;
        String rePhoneNumber;

        System.out.println("변경하지 않으시려면 엔터를 눌러주시기 바랍니다.");
        System.out.println("현재 사용자명 : " + checkUpdateUser.getName());
        System.out.print("변경하실 사용자명 : ");
        while(true) {
            reName = sc.nextLine().trim();
            /// regex rule
            /// \\S+ => 공백이 없을 때 true
            /// !rePassword.matches("\\S+")기에 공백이 포함되어 있으면 true를 반환
            if (!reName.matches("\\S+")) {
                System.out.println("사용자명은 공백 없이 입력해주세요.");
                continue;
            }
            if (reName.isEmpty()) reName = null;
            break;
        }
        System.out.println();

        System.out.print("변경하실 비밀번호 : ");
        while(true) {
            rePassword = sc.nextLine().trim();
            if (!rePassword.matches("\\S+")) {
                System.out.println("비밀번호에 공백을 포함할 수 없습니다.");
                continue;
            }
            if (rePassword.isEmpty()) rePassword = null;
            break;
        }
        System.out.println();

        System.out.println("현재 이메일 : " + checkUpdateUser.getEmail());
        System.out.print("변경하실 이메일 : ");
        while(true) {
            reMail = sc.nextLine().trim();
            /// regex rule
            /// 앞뒤로 공백이 없어야 하며 중간에 @와 .이 있어야 한다는 형식
            if (reMail.isEmpty() || !reMail.matches("\\S+@\\S+\\.\\S+")) {
                System.out.println("이메일 형식이 맞지 않습니다.");
                continue;
            }
            break;
        }
        System.out.println();

        System.out.println("현재 전화번호 : " + checkUpdateUser.getPhoneNumber());
        System.out.print("변경하실 전화번호 : ");

        while(true) {
            rePhoneNumber = sc.nextLine().trim();
            if (!rePhoneNumber.matches("\\S+") && (rePhoneNumber.length() == 10 || rePhoneNumber.length() == 11)) {
                System.out.println("잘못된 입력 형식입니다.");
                continue;
            }
            if(rePhoneNumber.isEmpty()) rePhoneNumber = null;
            break;
        }

        System.out.println("이대로 진행하시겠습니까?");
        System.out.println("맞으면 y, 취소하려면 n");
        System.out.println("다시 입력하시려면 re를 입력해주세요.");

        while(true) {
            String finalCheckIsContinue = sc.nextLine();
            switch (finalCheckIsContinue.toLowerCase()){
                case "y":
                    checkUpdateUser.updateUser(reName, rePassword, reMail, rePhoneNumber);
                    return;
                case "n":
                    return;
                case "re":
                    updateUserInfo(sc, checkUpdateUser);
                    return;
                default:
                    continue;
            }
        }
    }



    /// Read
    public User getUserId(UUID id) {
        return usersMap.get(id);
    }

    @Override
    public void getUserName(Scanner sc) {
        System.out.println("조회하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();

        if (usersName.get(name) == null) {
            System.out.println("조회하고자 하는 사용자가 없습니다.");
        }

        User u = usersMap.get(usersName.get(name));

        System.out.println("====================");
        System.out.println("사용자ID : " + u.getId());
        System.out.println("사용자명 : " + u.getName());
        System.out.println("이메일 : " + u.getEmail());
        System.out.println("전화번호 : " + u.getPhoneNumber());
        System.out.println("생성일 : " + u.getCreateAt());
        System.out.println("수정일 : " + u.getUpdateAt());
        System.out.println("====================");
    }

    public User getUserByName(String name) {
        try {
            return usersMap.get(usersName.get(name));
        } catch (Exception e) {
            return null;
        }
        /// null일 때 반환하는 로직 추가
    }

    public String getUserByName(UUID id) {
        return usersMap.get(id).getName();
    }

    @Override
    public void getAllUserName() {
        if (usersMap.isEmpty()) {
            System.out.println("사용자가 없습니다.");
            return;
        }

        usersMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(u -> {
            System.out.println("====================");
            System.out.println("사용자ID : " + u.getId());
            System.out.println("사용자명 : " + u.getName());
            System.out.println("이메일 : " + u.getEmail());
            System.out.println("전화번호 : " + u.getPhoneNumber());
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
        System.out.println("[Warning!] 계속 진행하시려면 아무 숫자나 입력해주세요.");

        n = sc.nextInt();
        sc.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        User target = check(sc, "삭제");

        if (target == null) {
            System.out.println("일치하는 계정을 찾을 수 없습니다.");
            return;
        }
        usersMap.remove(target.getId());
        usersName.remove(target.getName());
        System.out.println("계정이 삭제되었습니다.");
    }

    private User check(Scanner sc, String work) {
        System.out.println(work + "하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();

        User user = usersMap.get(usersName.get(name));

        if (user == null) return null;

        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();

        if (!user.getPw().equals(pw)) return null;

        return user;
    }
}
