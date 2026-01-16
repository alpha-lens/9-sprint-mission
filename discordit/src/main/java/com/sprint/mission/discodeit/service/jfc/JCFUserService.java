package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.Input;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserService implements UserService {
    private JCFUserService() {
    }
    private static class Holder {
        private static final JCFUserService INSTANCE = new JCFUserService();
    }
    public static JCFUserService getInstance() {
        return Holder.INSTANCE;
    }

    private final Map<UUID, User> usersMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> usersName = new ConcurrentHashMap<>();

    /// Create
    @Override
    public void createUser(Scanner sc) {
        String name;
        String pw;
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        name = sc.nextLine().trim();

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
        System.out.println("====================");
        System.out.println("사용자 변경 메뉴입니다.");
        User checkUpdateUser = check(sc, "변경");
        if(checkUpdateUser == null) {
            System.out.println("일치하는 사용자가 없습니다.");
            return;
        }

        updateUserInfo(sc, checkUpdateUser);
    }

    /// 사실 분리할 필요가 없긴 한데, 너무 길어져서 분리함
    /// FIXME: 더 좋은 리팩터링 방법이 필요
    private void updateUserInfo(Scanner sc, User checkUpdateUser) {
        String reName;
        String rePassword;
        String reMail;
        String rePhoneNumber;

        while(true) {
        /// 정규식은 있다는건 알아도 어떤 규칙인지 몰라서 GPT에게 물어봤습니다.
        System.out.println("변경하지 않으시려면 엔터를 눌러주시기 바랍니다.");

        reName = Input.inputUpdateField(sc, "사용자명", "\\S+", checkUpdateUser.getName());
        rePassword = Input.inputUpdateField(sc, "비밀번호", "\\S+", null);
        reMail = Input.inputUpdateField(sc, "이메일", "\\S+@\\S+\\.\\S+", checkUpdateUser.getEmail());
        rePhoneNumber = Input.inputUpdateField(sc, "전화번호", "^\\\\d{10,11}$", checkUpdateUser.getPhoneNumber());

        System.out.println("이대로 진행하시겠습니까?");
        System.out.println("맞으면 y, 다시 입력하려면 re");
        System.out.println("취소하려면 아무 키나 입력해주시기 바랍니다.");

            String finalCheckIsContinue = sc.nextLine();
            switch (finalCheckIsContinue.toLowerCase()){
                case "y":
                    checkUpdateUser.updateUser(reName, rePassword, reMail, rePhoneNumber);
                    return;
                case "re":
                    continue;
                default:
                    return;
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
            return;
        }

        User user = usersMap.get(usersName.get(name));

        System.out.println("====================");
        System.out.println("사용자ID : " + user.getId());
        System.out.println("사용자명 : " + user.getName());
        System.out.println("이메일 : " + user.getEmail());
        System.out.println("전화번호 : " + user.getPhoneNumber());
        System.out.println("생성일 : " + user.getCreateAt());
        System.out.println("수정일 : " + user.getUpdateAt());
        System.out.println("====================");
    }

    public User getUserByName(String name) {
        try {
            return usersMap.get(usersName.get(name));
        } catch (Exception e) {
            return null;
        }
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

        usersMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> {
            System.out.println("====================");
            System.out.println("사용자ID : " + user.getId());
            System.out.println("사용자명 : " + user.getName());
            System.out.println("이메일 : " + user.getEmail());
            System.out.println("전화번호 : " + user.getPhoneNumber());
            System.out.println("생성일 : " + user.getCreateAt());
            System.out.println("수정일 : " + user.getUpdateAt());
            System.out.println("====================");
        });
        System.out.println("현재 총 사용자 : " + usersMap.size());
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
