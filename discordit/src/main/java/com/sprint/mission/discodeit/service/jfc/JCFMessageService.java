package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService {
    private final List<Message> msgs = new ArrayList<>();

    public void createMessage(Scanner sc, Channel ch, User user) {
        System.out.println("현재 메시지를 보낼 채널은 " + ch.getName() + "입니다.");
        System.out.println("현재 메시지를 보낼 사람은 " + user.getName() + "입니다.");
        System.out.println("무어라 보내고 싶으신가요?");

        String text = sc.nextLine();

        System.out.println("현재 채널에 '" + text + "'라고 보내려 합니다.");
        System.out.println("보내시려면 1을, 아니라면 0을 입력해주세요");
        String n = sc.nextLine();
        if(Objects.equals(n, "0")) {
            System.out.println("처음으로 돌아갑니다.");
        } else if (Objects.equals(n, "1")) {
            msgs.add(new Message(ch.getId(), user.getId(), text));
            System.out.println("성공.");
        } else {
            System.out.println("잘못 입력했습니다. 다시 메시지 입력 부분으로 돌아갑니다.");
            createMessage(sc, ch, user);
        }
    }

    /// update logic
    public void updateMessage(Scanner sc, User user) {
        readMsgForUser(user);

        System.out.println("어떤 것을 수정하고 싶나요?");
        String id = sc.nextLine();
        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String context = sc.nextLine();
        System.out.println("지금 작업중입니다.");

        for(Message m : msgs) {
            if(m.getId().equals(UUID.fromString(id))){
                m.setContent(context);
                System.out.println("성공!");
                return;
            }
        }
        System.out.println("해당 내용을 찾지 못했습니다.");
    }

    public void readMsgInChannel(Channel ch) {
        UUID chId = ch.getId();
        boolean capture = false;

        for(Message msg : msgs) {
            if(msg.getSendChannel().equals(chId)) {
                System.out.println("보낸 사용자 : " + msg.getSendUserId());
                System.out.println("보낸 내용 : " + msg.getContent());
                capture = true;
            }
        }

        if (!capture) {
            System.out.println("해당 서버에 보낸 메시지가 없어요.");
        }
    }

    public void readMsgInChannel(UUID chId) {
        boolean capture = false;

        for(Message msg : msgs) {
            if(msg.getSendChannel().equals(chId)) {
                System.out.println("보낸 사용자 : " + msg.getSendUserId());
                System.out.println("보낸 내용 : " + msg.getContent());
                capture = true;
            }
        }

        if (!capture) {
            System.out.println("해당 서버에 보낸 메시지가 없어요.");
        }
    }

    public void readMsgForUser(User user) {
        System.out.println("당신이 보낸 메시지는 아래와 같습니다.");
        UUID uid = user.getId();
        List<Message> msgList = new ArrayList<>();
        for(Message msg : msgs) {
            if(msg.getSendUserId() == uid) {
                msgList.add(msg);
            }
        }

        if(msgList.isEmpty()) {
            System.out.println("아쉽지만 아무것도 없네요!");
            return;
        }

        for(Message msg : msgList) {
            System.out.println("ID : " + msg.getId());
            System.out.println("ChannelID : " + msg.getSendChannel());
            System.out.println("Context : " + msg.getContent());
            System.out.println("===============");
        }
    }

    public void deleteMsg(Scanner sc, User user) {
        readMsgForUser(user);
        System.out.println("어떤 메시지를 삭제하고 싶나요? ID로 입력해주세요.");
        String id = sc.nextLine();

        UUID targetId = UUID.fromString(id);
        for (Message msg : msgs) {
            if (msg.getId().equals(targetId)) {
                System.out.println("해당 메시지를 삭제합니까? : Y or any Key");
                String ins = sc.nextLine();

                if (ins.toUpperCase().equals("Y")) {
                    msgs.remove(msg);
                    System.out.println("성공!");
                } else {
                    System.out.println("초기로 돌아갑니다");
                    return;
                }
            }
        }
        System.out.println("해당 ID를 가진 메시지를 찾을 수 없습니다.");
    }
}
