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

    @Override
    public void createServer(Scanner sc) {
        System.out.println("사용하려는 서버명이 무엇인가요?");
        String name = sc.nextLine();
        if (check(name) == null) {
            channels.add(new Channel(name));
            System.out.println("잘 들어갔어요!");
            return;
        }
        System.out.println("이미 존재하는 채널명이에요!");
    }

    @Override
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

    @Override
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

    @Override
    public Channel readServer(String name) {
        Channel result =  check(name);

        if(result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
        return result;
    }

    @Override
    public String readServer(UUID id) {
        Channel result = check(id);

        if(result == null) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return null;
        }
        return result.getName();
    }

    @Override
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

    @Override
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

    @Override
    public Channel check(String name) {
//        for (Channel u : channels) {
//            if (u.getName().equals(name)) {
//                return u;
//            }
//        }
//        return null;
        return channels.stream().filter(u -> u.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public Channel check(UUID id) {
        return channels.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
