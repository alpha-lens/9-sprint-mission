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
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("프로그램을 실행합니다.");
        boolean run = true;
        int menu = 0;
        Scanner sc = new Scanner(System.in);

        JCFUserService us = new JCFUserService();
        JCFChannelService cs = new JCFChannelService();
        JCFMessageService ms = new JCFMessageService();

        /// Test Code
        while (run) {
            System.out.println("==========");
            System.out.println("0. 프로그램 종료하기");
            System.out.println("1. 사용자 관련 서비스");
            System.out.println("2. 채널 관련 서비스");
            System.out.println("3. 메시지 관련 서비스");
            System.out.println("==========");
            menu = sc.nextInt();
            sc.nextLine();

            if (menu == 0) return;

            JavaApplication.route(sc, menu, us, cs, ms);

//            switch (menu) {
//                case 1:
//                    us.createUser(sc);
//                    break;
//                case 2:
//                    us.getAllUserName();
//                    break;
//                case 3:
//                    System.out.println("생성하고자 하는 채널명을 알려주세요");
//                    name = sc.nextLine();
//                    cs.createServer(name);
//                    break;
//                case 4:
//                    cs.readAllServer();
//                    break;
//                case 5:
//                    System.out.println("어느 채널의 메시지를 확인할까요?");
//                    cs.readAllServer();
//                    System.out.println("ID 값을 복사해서 넣어주세요.");
//                    name = sc.nextLine();
//                    ms.readMsgInChannel(UUID.fromString(name));
//                    break;
//                case 6:
//                    break;
//                default:
//                    System.out.println("Bye bye");
//                    run = false;
//            }
        }
    }

    private static void route(Scanner sc, int n, JCFUserService us, JCFChannelService cs, JCFMessageService ms){
        int m;
        switch(n) {
            case 1:
                System.out.println("사용자 관련 서비스입니다.");
                System.out.println("어떤 서비스를 원하시나요?");
                System.out.println("=====================");
                System.out.println("1. User Create");
                System.out.println("2. User Update");
                System.out.println("3. User Read");
                System.out.println("4. User Delete");
                m = sc.nextInt();
                sc.nextLine();
                userService(sc, m, us);
                break;
            case 2:
                System.out.println("채널 관련 서비스입니다.");
                System.out.println("어떤 서비스를 원하시나요?");
                System.out.println("=====================");
                System.out.println("1. Channel Create");
                System.out.println("2. Channel Update");
                System.out.println("3. Channel Read");
                System.out.println("4. Channel Delete");
                m = sc.nextInt();
                sc.nextLine();
                channelService(sc, m, cs);
                break;
            case 3:
                System.out.println("메시지 관련 서비스입니다.");
                System.out.println("어떤 서비스를 원하시나요?");
                System.out.println("=====================");
                System.out.println("1. Message Create");
                System.out.println("2. Message Update");
                System.out.println("3. Message Read");
                System.out.println("4. Message Delete");
                m = sc.nextInt();
                sc.nextLine();
                messageService(sc, m, us,cs,ms);
                break;
            default:
                return;
        }
    }

    private static void userService(Scanner sc, int n, JCFUserService us) {
        int m;
        switch(n) {
            case 1:
                /// create
                us.createUser(sc);
                break;
            case 2:
                /// update
                us.updateUser(sc);
                break;
            case 3:
                /// read
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 사용자만 가져옵니다");
                System.out.println("2 : 전체 사용자를 가져옵니다");
                m = sc.nextInt();
                sc.nextLine();

                if(m == 1) us.getUserName(sc);
                else if(m == 2) us.getAllUserName();
                break;
            case 4:
                /// delete
                us.deleteUser(sc);
                break;
            default:
                return;
        }
    }
    private static void channelService(Scanner sc, int n, JCFChannelService cs) {
        int m;
        switch(n) {
            case 1:
                /// create
                cs.createServer(sc);
                break;
            case 2:
                /// update
                cs.updateServer(sc);
                break;
            case 3:
                /// read
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 채널만 가져옵니다");
                System.out.println("2 : 전체 채널을 가져옵니다");
                m = sc.nextInt();
                sc.nextLine();

                if(m == 1) cs.readServer(sc);
                else if(m == 2) cs.readAllServer();
                break;
            case 4:
                /// delete
                cs.deleteServer(sc);
                break;
            default:
                return;
        }
    }
    private static void messageService(Scanner sc, int n, JCFUserService us, JCFChannelService cs, JCFMessageService ms) {
        int m;
        String uName;
        User nowUser;
        String cName;
        Channel nowChannel;
        switch(n) {
            case 1:
                /// create
                System.out.println("누가 보내는 메시지인가요?");
                uName = sc.nextLine();
                nowUser = us.getUserName(uName);
                if(nowUser == null) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }

                System.out.println("어디로 메시지인가요?");
                cName = sc.nextLine();
                nowChannel = cs.readServer(cName);
                if(nowChannel == null) {
                    System.out.println("존재하지 않는 채널입니다.");
                    return;
                }

                ms.createMessage(sc, nowChannel, nowUser);
                break;
            case 2:
                /// update
                System.out.println("누가 보낸 메시지인가요?");
                uName = sc.nextLine();
                nowUser = us.getUserName(uName);
                if(nowUser == null) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                ms.updateMessage(sc, nowUser, cs, us);
                break;
            case 3:
                /// read
                System.out.println("현재는 내가 보낸 메시지, 특정 채널 메시지를 조회하는 기능만 있습니다.");
                System.out.println("1. 내가 보낸 메시지 확인하기");
                System.out.println("2. 채널에 있는 메시지 확인하기");
                m = sc.nextInt();
                sc.nextLine();

                if(m == 1) {
                    System.out.println("누가 보낸 메시지인가요?");
                    uName = sc.nextLine();
                    nowUser = us.getUserName(uName);
                    if(nowUser == null) {
                        System.out.println("존재하지 않는 사용자입니다.");
                        return;
                    }
                    ms.readMsgForUser(nowUser, cs, us);
                } else if (m == 2) {
                    System.out.println("어디로 메시지인가요?");
                    cName = sc.nextLine();
                    nowChannel = cs.readServer(cName);
                    if(nowChannel == null) {
                        System.out.println("존재하지 않는 채널입니다.");
                        return;
                    }
                    ms.readMsgInChannel(nowChannel.getId(), us);
                }
                break;
            case 4:
                /// delete
                System.out.println("현재는 내가 보낸 메시지를 삭제하는 기능만 지원하고 있습니다.");
                System.out.println("사용자명은 어떻게 되나요?");
                uName = sc.nextLine();
                nowUser = us.getUserName(uName);
                if(nowUser == null) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                ms.deleteMsg(sc, nowUser, cs, us);
                break;
            default:
                return;
        }
    }

    public static void chIdToName(){

    }
    public static void uIdToName(JCFUserService us, UUID id){
        User nowUser = us.getUserId(id);
        if(nowUser == null) {
            System.out.println("존재하지 않는 사용자입니다.");
        }
    }
}
