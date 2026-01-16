package com.sprint.mission.discodeit.app.router;

import java.util.ArrayList;
import java.util.List;

public class RoutePrintText {
    static void printText(String text) {
        List<String> retouchText = new ArrayList<>();

        switch(text.toLowerCase()) {
            case "user":
                retouchText.add("User");
                retouchText.add("사용자");
                break;

            case "channel":
                retouchText.add("Channel");
                retouchText.add("채널");
                break;

            case "message":
                retouchText.add("Message");
                retouchText.add("메세지");
                break;

            default:
                return;
        }

        System.out.println(retouchText.get(0) + " 관련 서비스입니다.");
        System.out.println("어떤 서비스를 원하시나요?");
        System.out.println("=====================");
        System.out.println("1. " + retouchText.get(1) + " Create");
        System.out.println("2. " + retouchText.get(1) + " Update");
        System.out.println("3. " + retouchText.get(1) + " Read");
        System.out.println("4. " + retouchText.get(1) + " Delete");
    }
}
