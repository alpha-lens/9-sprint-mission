package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jfc.JCFChannelService;
import com.sprint.mission.discodeit.service.jfc.JCFMessageService;
import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("프로그램을 실행합니다.");
        boolean run = true;
        boolean menu;
        Scanner sc = new Scanner(System.in);

        JCFMessageService ms = new JCFMessageService();
        JCFUserService us = new JCFUserService();
        JCFChannelService cs = new JCFChannelService();

        /// User getname 부분 잘 되는지 테스트하기
        us.createUser("김경한", "rlarudgks");
        us.createUser("강감찬", "rkdrkacks");
        us.createUser("이순신", "dltnstls");
        us.getAllUserName();

        /// User edit 부분 테스트하기
//        us.updateUser(sc);
//        us.getAllUserName();
        /// User delete 부분 테스트하기
//        us.deleteUser(sc);
//        us.getAllUserName();

        /// Server
        /// Create, Update Test 완료
        cs.createServer("스프린트");
        cs.createServer("커뮤니티");
        cs.createServer("김경한의 이모저모");
        cs.createServer("강호동의 헛소리");
//        cs.updateServer(sc);

        /// Read, Delete
//        cs.readServer(sc);
//        cs.deleteServer(sc);
        cs.readAllServer();
    }
}
