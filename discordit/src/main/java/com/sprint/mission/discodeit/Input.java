package com.sprint.mission.discodeit;

import java.util.Scanner;

public class Input {
    public static String inputChecker(Scanner scanner, String regexRule) {
        String inputString;
        while(true) {
            inputString = scanner.nextLine().trim();

            if (inputString.isEmpty()) return null;

            if (!inputString.matches(regexRule)) {
                System.out.println("잘못된 입력 형식입니다.");
                continue;
            }

            break;
        }
        return inputString;
    }

    public static String inputUpdateField(Scanner sc, String text, String regexRule) {
        if(text.equals("비밀번호")) {
            System.out.print("변경하실 비밀번호 : ");
            return inputChecker(sc, regexRule);
        }

        System.out.print("변경하실 " + text +  " : ");
        return inputChecker(sc, regexRule);
    }
}
