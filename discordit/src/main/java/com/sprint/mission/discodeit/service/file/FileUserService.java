package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Input;
import com.sprint.mission.discodeit.app.JavaApplication;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileUserService implements UserService {
    Scanner sc = JavaApplication.scanner();

    private FileUserService() {
    }
    private static class Holder {
        private static final FileUserService INSTANCE = new FileUserService();
    }
    public static FileUserService getInstance() {return Holder.INSTANCE;}

    FileUserRepository fileUserRepository = FileUserRepository.getInstance();

    @Override
    public void createUser() {
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        String name = sc.nextLine().trim();

        if (fileUserRepository.userNameToId(name) != null) {
            System.out.println("이미 존재하는 사용자명입니다.");
            return;
        }

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        String pw = sc.nextLine();

        if(fileUserRepository.createUser(name, pw)) {
            System.out.println("사용자 " + name + "님이 추가되었습니다.");
        } else {
            System.err.println("알 수 없는 오류가 발생하여 저장하지 못했습니다.");
        }
    }

    /// Update
    @Override
    public void updateUser() {
        System.out.println("====================");
        System.out.println("사용자 변경 메뉴입니다.");
        UUID userId = workRoute("변경");
        if(userId == null) {
            System.out.println("일치하는 사용자가 없습니다.");
            return;
        }

        while(true) {
            System.out.println("변경하지 않으실 필드에는 엔터를 눌러주시기 바랍니다.");

            String reName = Input.inputUpdateField(sc, "사용자명", "\\S+");
            String rePassword = Input.inputUpdateField(sc, "비밀번호", "\\S+");
            String reMail = Input.inputUpdateField(sc, "이메일", "\\S+@\\S+\\.\\S+");
            String rePhoneNumber = Input.inputUpdateField(sc, "전화번호", "^\\d{10,11}$");

            System.out.println("이대로 진행하시겠습니까?");
            System.out.println("맞으면 y, 다시 입력하려면 re");
            System.out.println("취소하려면 아무 키나 입력해주시기 바랍니다.");

            String finalCheckIsContinue = sc.nextLine();
            switch (finalCheckIsContinue.toLowerCase()){
                case "y":
                    if(fileUserRepository.updateUser(userId, reName, rePassword, reMail, rePhoneNumber)) {
                        System.out.println("완료");
                    }
                    System.out.println("실패");
                    return;
                case "re":
                    continue;
                default:
                    return;
            }
        }
    }

    /// Read
    @Override
    public void getUserName() {
        System.out.println("조회하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();

        if (fileUserRepository.userNameToId(name) == null) {
            System.out.println("조회하고자 하는 사용자가 없습니다.");
            return;
        }

        System.out.println(fileUserRepository.getUser(name));
        System.out.println("====================");
    }

    @Override
    public void getAllUserName() {
        List<String> userList = fileUserRepository.getAllUser();
        if (userList.isEmpty()) {
            System.out.println("사용자가 없습니다.");
            return;
        }

        userList.forEach(System.out::println);
        System.out.println("현재 총 사용자 : " + userList.size());
    }

    /// Delete
    @Override
    public void deleteUser() {
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.out.println("[Warning!] 계속 진행하시려면 아무 숫자나 입력해주세요.");

        int n = sc.nextInt();
        sc.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        UUID userId = workRoute("삭제");

        if (userId == null) {
            System.out.println("일치하는 계정을 찾을 수 없습니다.");
            return;
        }

        fileUserRepository.deleteUser(userId);

        System.out.println("계정이 삭제되었습니다.");
    }

    private UUID workRoute(String work) {
        System.out.println(work + "하고자 하는 사용자명을 입력해주세요");
        String name = sc.nextLine();
        UUID userId = fileUserRepository.userNameToId(name);

        if (userId == null) return null;

        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String pw = sc.nextLine();

        if (fileUserRepository.check(userId, pw)) return null;

        return userId;
    }
}
