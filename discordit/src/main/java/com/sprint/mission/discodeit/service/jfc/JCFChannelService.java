package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels = new ArrayList<>();

    /// create
        public void createServer(String name) {
        channels.add(new Channel(name));
    }

    public void updateServer(Scanner sc) {
        System.out.println("변경하고자 하는 채널명을 알려주세요");
        String name = sc.nextLine();

        Channel result = check(name);

        if(result == null) {
            System.out.println("해당 서버가 존재하지 않습니다.");
            return;
        }

        System.out.println("현재 채널명 : " + name);
        System.out.println("무엇으로 변경하고 싶은가요? ");

        name = sc.nextLine();

        result.setName(name);
        System.out.println(result.getName());
        System.out.println("잘 변경되었어요!");
    }

    public void readServer(Scanner sc) {
        System.out.println("조회하고자 하는 채널명을 입력해주세요");
        String name = sc.nextLine();
        Channel result =  check(name);

        if(result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return;
        }

        System.out.println("해당 채널에 대한 정보를 알려드립니다.");
        System.out.println("채널명 : " + result.getName());
        System.out.println("채널ID : " + result.getId());
        System.out.println("채널생성일 : " + result.getCreateAt());
        System.out.println("채널수정일 : " + result.getUpdateAt());
        System.out.println("===================");
    }

    public void readAllServer() {
        for (Channel ch : channels) {
            System.out.println("채널명 : " + ch.getName());
            System.out.println("채널ID : " + ch.getId());
            System.out.println("채널수정일 : " + ch.getUpdateAt());
            System.out.println("채널생성일 : " + ch.getCreateAt());
            System.out.println("===================");
        }
        System.out.println("현재 총 채널수 : " + channels.size());
    }

    public void deleteServer(Scanner sc) {
        String name;
        int n;
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.out.println("[Warning!] 계속 진행하려면 아무 숫자나 입력해주세요");

        n = sc.nextInt();
        sc.nextLine();
        if(n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        };

        System.out.print("삭제하려는 채널명을 알려주세요: ");
        name = sc.nextLine();

        Channel target = check(name);

        if (target != null) {
            channels.remove(target);
            System.out.println("채널이 삭제되었습니다.");
        } else {
            System.out.println("일치하는 채널을 찾을 수 없습니다.");
        }
    }

    public Channel check(String name) {
        for (Channel u : channels) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }
}
