package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class Input {
    private final Scanner scanner;
    private final FileUserRepository userRepository;

    private String inputChecker(String text, String regexRule) {
        String inputString;
        while(true) {
            inputString = scanner.nextLine().trim();

            if (inputString.isEmpty()) return null;

            if (!inputString.matches(regexRule)) {
                System.err.println("잘못된 입력 형식입니다.");
                continue;
            }

            if (text.equals("프로필 이미지") && inputString.matches("(?i).*\\.(jpg|png)")) {
                System.err.println("이미지는 jpg, png만 지원합니다.");
                continue;
            }

            if (userRepository.isPresentThis(text, inputString)) {
                System.err.println("동일한 " + text + "은(는) 존재할 수 없습니다.");
                continue;
            }

            break;
        }
        return inputString;
    }

    public String inputUpdateField(String text, String regexRule) {
        System.out.print("변경하실 " + text + " : ");
        if (text.equals("프로필 이미지")){
            System.out.print("이미지 확장자는 jpg, png만 지원합니다.");
        }
        return inputChecker(text, regexRule);
    }
}
