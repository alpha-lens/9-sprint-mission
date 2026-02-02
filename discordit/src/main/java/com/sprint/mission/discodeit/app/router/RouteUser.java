package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.Input;
import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class RouteUser {
    private final BasicUserService userService;
    private final BasicBinaryContentService binaryContentService;
    private final Scanner scanner;
    private final IsLogin isLogin;
    private final Input input;
    private final UserState userState;

    public void route(int routeCRUD) {
        int menu;
        if(!isLogin.check("User", routeCRUD)) {
            System.err.println("해당 기능은 로그인한 후 이용 가능합니다.");
            return;
        }

        switch (routeCRUD) {
            /// create
            case 1:
                create();
                break;

            /// update
            case 2:
                update();
                break;

            /// read
            case 3:
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 사용자만 가져옵니다");
                System.out.println("2 : 전체 사용자를 가져옵니다");

                menu = scanner.nextInt();
                scanner.nextLine();

                if (menu == 1) find();
                else if (menu == 2) findAll();
                break;

            /// delete
            case 4:
                delete();
                break;

            default:
                return;
        }
    }

    private void create() {
        System.out.println("====================");
        System.out.println("회원가입에 오신 것을 환영합니다.");
        System.out.print("먼저, 사용할 이름을 작성해주세요 : ");
        String name = scanner.nextLine().trim();

        if (userService.isPresent(name)) {
            System.out.println("이미 존재하는 사용자명입니다.");
            return;
        }

        System.out.print("사용할 비밀번호를 입력해주세요 : ");
        String password = scanner.nextLine();

        System.out.print("사용할 프로필 이미지를 입력해주세요 : ");
        String profileImage = scanner.nextLine();
        if(userService.create(new CreateUserDto(name, password, profileImage))) {
            System.out.println("성공적으로 추가했습니다.");
        } else System.err.println("알 수 없는 오류로 실패했습니다.");
    }

    private void update() {
        System.out.println("====================");
        System.out.println("사용자 변경 메뉴입니다.");
        System.out.println("현재 로그인한 사용자의 비밀번호를 입력해주세요");
        String password = scanner.nextLine();
        if(!userService.isValid(password)) {
            System.err.println("비밀번호가 일치하지 않습니다. 처음으로 돌아갑니다.");
        }

        while(true) {
            System.out.println("변경하지 않으실 필드에는 엔터를 눌러주시기 바랍니다.");

            String reName = input.inputUpdateField("사용자명", "\\S+");
            String rePassword = input.inputUpdateField("비밀번호", "\\S+");
            String reMail = input.inputUpdateField("이메일", "\\S+@\\S+\\.\\S+");
            String rePhoneNumber = input.inputUpdateField("전화번호", "^\\d{10,11}$");
            String reProfile = input.inputUpdateField("프로필 이미지", "\\.");

            System.out.println("이대로 진행하시겠습니까?");
            System.out.println("맞으면 y, 다시 입력하려면 re");
            System.out.println("취소하려면 n을 입력해주시기 바랍니다.");

            String finalCheckIsContinue = scanner.nextLine();

            if(finalCheckIsContinue.equalsIgnoreCase("re")) continue;
            if(finalCheckIsContinue.equalsIgnoreCase("n")) return;

            if(finalCheckIsContinue.equalsIgnoreCase("y") && userService.update(reName, rePassword, reMail, rePhoneNumber, reProfile)) {
                System.out.println("성공적으로 변경되었습니다.");
            }

            System.err.println("알 수 없는 오류로 인해 실패했습니다.");
        }
    }

    private void find() {
        System.out.println("조회하고자 하는 사용자명을 입력해주세요");
        String name = scanner.nextLine();

        try {
            System.out.println(userService.find(name));
            System.out.println("====================");
        } catch (NotFound e) {
            System.err.println("[ERROR]" + e);
        }
    }

    private void findAll() {
        if(userService.findAll().isEmpty()) {
            System.err.println("사용자를 찾을 수 없습니다.");
        }

        List<String> findAllUser = userService.findAll();

        findAllUser.forEach(System.out::println);
        System.out.println("총 사용자 : " + findAllUser.size());
    }

    private void delete() {
        System.err.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.err.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.err.println("[Warning!] 계속 진행하시려면 아무 숫자나 입력해주세요.");

        int n = scanner.nextInt();
        scanner.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        System.out.println("현재 로그인한 사용자의 비밀번호를 입력해주세요.");
        String password = scanner.nextLine();

        if(!userService.isValid(password)) {
            System.err.println("비밀번호가 일치하지 않습니다. 처음으로 돌아갑니다.");
        }

        if(userService.delete(userState.getUserId())) {
            userState.userState("");
            System.out.println("성공적으로 삭제되었습니다.");
        }
    }
}
