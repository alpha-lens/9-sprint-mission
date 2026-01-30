package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final FileUserRepository userRepository;
    private final Scanner scanner;
    private final UserState userState;

    public boolean login() {
        System.out.println("로그인 서비스입니다.");
        System.out.println("로그인할 사용자명을 입력해주세요");
        String name = scanner.nextLine().trim();
        System.out.println("해당 사용자의 비밀번호를 입력해주세요");
        String password = scanner.nextLine().trim();

        /// 검증로직
        UUID id = userRepository.userNameToId(name);

        if(!userRepository.check(id, password)) {
            userState.userState(name, id);
            System.out.println("성공!");
            return true;
        } else {
            System.err.println("실패, 다시 확인해주시기 바랍니다.");
            return false;
        }
    }

    public void logout() {
        userState.userState("");
    }
}
