package com.sprint.mission.discodeit.entity_.service.channel;

import java.util.Scanner;
import java.util.List;

public class CreateServer {
    public static void createServer(Scanner sc, List<Server> server) {
        System.out.println("서버 생성에 오신 것을 환영합니다!");
        System.out.println("사용하려는 서버명을 입력해주세요!");
        String serverName = sc.nextLine();
        if(Server.duplicateCheck(server, serverName)) {
            System.out.println("잘 들어갔어요!");
            server.add(new Server(serverName));
        }
        else System.out.println("[Error!] 이미 존재하는 서버명입니다.");
    }
}
