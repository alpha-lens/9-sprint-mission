package com.sprint.mission.discodeit.entity_.service.channel;

import com.sprint.mission.discodeit.entity_.UUIDGenerate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
    public static boolean duplicateCheck(List<Server> list, String name) {
        int temp = list.stream().filter(e -> e.getName().equals(name)).toArray().length;

        return temp == 0;
    }

    private String id;
    private final List<String> channel = new ArrayList<>();
    private final long createAt;
    private long updateAt;
    private String name;
    Scanner sc = new Scanner(System.in);

    public Server(String name) {
        this.id = new UUIDGenerate().toString();
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
        this.name = name;
    }

    public String getId() { return id; }
    public long getCreateAt() {
        return createAt;
    }
    public long getUpdateAt() {
        return updateAt;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }

    public void createChannel() {
        System.out.println("지금은 " + this.name
                + " 서버에 새로운 채팅 채널을 생성하려 합니다.");
        System.out.println("채널명은 무엇인가요? : ");
        String channel = sc.nextLine();
        this.channel.add(channel);
    };

    public void renameChannel() {
        System.out.println("지금은 " + this.name
                + " 서버에서 채팅 채널명을 수정하려 합니다.");
        System.out.println("무엇을 수정하고 싶은가요?");
        String channel = sc.nextLine();
        System.out.println("지금 입력한 채널명은 " + channel + "입니다. 맞나요?");
        String test = sc.nextLine().toUpperCase();
        if(test.equals("Y")) {
            if(channel.contains(channel)) {
                System.out.println("어머, 이미 존재하는 채널이에요!");
            } else {
                System.out.println("오, 좋은 이름이네요!");
                System.out.println("잠시만 기다려보세요! 지금 열심히 일하는 중입니다.");
                setName(channel);
                try{
                    Thread.sleep(4000);
                    System.out.println(getName());
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            };
        } else {
            System.out.println("다시 입력하시려 하는군요. 좋아요.");
            renameChannel();
        }
    }
}
