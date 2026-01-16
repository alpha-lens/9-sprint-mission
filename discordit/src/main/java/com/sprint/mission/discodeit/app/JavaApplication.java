package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jfc.JCFChannelService;
import com.sprint.mission.discodeit.service.jfc.JCFMessageService;
import com.sprint.mission.discodeit.service.jfc.JCFUserService;

import java.util.Scanner;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("프로그램을 실행합니다.");
        boolean run = true;
        int menu;
        Scanner sc = new Scanner(System.in);

        /// 20260115 멘토링 때, 싱글톤 패턴이 적용되지 않았다고 해서 수정함
        /// 아직은 왜 싱글톤인지, 왜 필요한지를 잘 모르겠다.
        JCFUserService us = JCFUserService.getInstance();
        JCFChannelService cs = JCFChannelService.getInstance();
        JCFMessageService ms = JCFMessageService.getInstance();

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
        }
    }

    private static void route(Scanner sc, int n, JCFUserService us, JCFChannelService cs, JCFMessageService ms) {
        int m;
        switch (n) {
            /// TODO: 중복 출력 부분 별도 로직으로 분리하기
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
                messageService(sc, m, us, cs, ms);
                break;
            default:
                return;
        }
    }

    private static void userService(Scanner sc, int n, JCFUserService us) {
        int m;
        switch (n) {
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

                if (m == 1) us.getUserName(sc);
                else if (m == 2) us.getAllUserName();
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
        switch (n) {
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

                if (m == 1) cs.readChannel(sc);
                else if (m == 2) cs.readAllChannel();
                break;
            case 4:
                /// delete
                cs.deleteChannel(sc);
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
        switch (n) {
            case 1:
                /// create
                System.out.println("누가 보내는 메시지인가요?");
                uName = sc.nextLine();
                try {
                    nowUser = us.getUserByName(uName);
                } catch (Exception e) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }

                System.out.println("어디로 보내는 메시지인가요?");
                cName = sc.nextLine();
                try {
                    nowChannel = cs.readChannel(cName);
                } catch (Exception e) {
                    System.out.println("존재하지 않는 채널입니다.");
                    return;
                }

                ms.createMessage(sc, nowChannel, nowUser);
                break;
            case 2:
                /// update
                System.out.println("누가 보낸 메시지인가요?");
                uName = sc.nextLine();
                nowUser = us.getUserByName(uName);
                if (nowUser == null) {
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

                if (m == 1) {
                    System.out.println("누가 보낸 메시지인가요?");
                    uName = sc.nextLine();
                    nowUser = us.getUserByName(uName); /// TODO: 직관적 이름 선정하기
                    if (nowUser == null) {
                        System.out.println("존재하지 않는 사용자입니다.");
                        return;
                    }
                    ms.readMsgForUser(nowUser, cs, us);
                } else if (m == 2) {
                    System.out.println("어디로 보내는 메시지인가요?");
                    cName = sc.nextLine();
                    nowChannel = cs.readChannel(cName);
                    if (nowChannel == null) {
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
                nowUser = us.getUserByName(uName);
                if (nowUser == null) {
                    System.out.println("존재하지 않는 사용자입니다.");
                    return;
                }
                ms.deleteMsg(sc, nowUser, cs, us);
                break;
            default:
                return;
        }
    }

///    뭔가 하려고 넣었는데 왜 넣었는지 기억이 안 남.. 지금은 미사용.
//    public static void chIdToName(JCFChannelService cs, UUID id) {
//        String nowChannel = cs.readChannel(id);
//        if (nowChannel == null) {
//            System.out.println("존재하지 않는 채널입니다.");
//        }
//    }
//
//    public static void uIdToName(JCFUserService us, UUID id) {
//        User nowUser = us.getUserId(id);
//        if (nowUser == null) {
//            System.out.println("존재하지 않는 사용자입니다.");
//        }
//    }
}
