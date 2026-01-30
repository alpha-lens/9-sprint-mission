package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Input;
import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final Scanner scanner;
    private final FileUserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final Input Input;
    private final UserState userState;

    @Override
    public void createUser() {
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        String name = scanner.nextLine().trim();

        if (userRepository.userNameToId(name) != null) {
            System.out.println("이미 존재하는 사용자명입니다.");
            return;
        }

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        String password = scanner.nextLine();

        System.out.print("사용할 프로필 이미지를 입력해주세요 : ");
        String profileImage = scanner.nextLine();
        CreateUserDto requestDto = new CreateUserDto(name, password, profileImage);

        if(userRepository.createUser(requestDto)) {
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
        UUID userId = workRoute();
        if(userId == null) {
            System.err.println("일치하지 않습니다. 처음으로 돌아갑니다.");
            return;
        }

        while(true) {
            System.out.println("변경하지 않으실 필드에는 엔터를 눌러주시기 바랍니다.");

            String reName = Input.inputUpdateField("사용자명", "\\S+");
            String rePassword = Input.inputUpdateField("비밀번호", "\\S+");
            String reMail = Input.inputUpdateField("이메일", "\\S+@\\S+\\.\\S+");
            String rePhoneNumber = Input.inputUpdateField("전화번호", "^\\d{10,11}$");
            String reProfile = Input.inputUpdateField("프로필 이미지", "\\.");

            System.out.println("이대로 진행하시겠습니까?");
            System.out.println("맞으면 y, 다시 입력하려면 re");
            System.out.println("취소하려면 아무 키나 입력해주시기 바랍니다.");

            String finalCheckIsContinue = scanner.nextLine();
            switch (finalCheckIsContinue.toLowerCase()){
                case "y":
                    if(userRepository.updateUser(userId, reName, rePassword, reMail, rePhoneNumber)) {
                        if(reProfile != null){
                            attachmentRepository.delete(userId);
                            attachmentRepository.create(userId, reProfile);
                        }
                        System.out.println("완료");
                        return;
                    }
                    System.err.println("실패");
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
        String name = scanner.nextLine();

        if (userRepository.userNameToId(name) == null) {
            System.out.println("조회하고자 하는 사용자가 없습니다.");
            return;
        }

        System.out.println(userRepository.getUser(name));
        System.out.println("====================");
    }

    @Override
    public void getAllUserName() {
        List<String> userList = userRepository.getAllUser();
        if (userList.isEmpty()) {
            System.err.println("사용자가 없습니다.");
            return;
        }

        userList.forEach(System.out::println);
        System.out.println("현재 총 사용자 : " + userList.size());
    }

    /// Delete
    @Override
    public void deleteUser() {
        System.err.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.err.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.err.println("[Warning!] 계속 진행하시려면 아무 숫자나 입력해주세요.");

        int n = scanner.nextInt();
        scanner.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        UUID userId = workRoute();

        if (userId == null) {
            System.err.println("일치하는 계정을 찾을 수 없습니다.");
            return;
        }

        userRepository.deleteUser(userId);

        System.out.println("계정이 삭제되었습니다.");
    }

    private UUID workRoute() {
        UUID userId = userRepository.userNameToId(userState.getUserName());

        System.out.println("현재 로그인한 " + userState.getUserName() +"의 비밀번호를 입력해주세요");
        String pw = scanner.nextLine();

        if (userRepository.check(userId, pw)) return null;

        return userId;
    }
}
