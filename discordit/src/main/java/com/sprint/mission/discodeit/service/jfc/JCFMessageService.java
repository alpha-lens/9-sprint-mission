package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

/// TODO: 채널 메시지 조회헤엇
/// 구분자, 전체 메시지 개수 넣어주기

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
    public void updateMessage(Scanner sc, User user, JCFChannelService cs, JCFUserService us) {
        readMsgForUser(user, cs, us);

        System.out.println("어떤 것을 수정하고 싶나요?");
        String id = sc.nextLine();

        Message text = msgs.stream().filter(m -> m.getId().equals(UUID.fromString(id))).findFirst().orElse(null);

        if(text == null) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String context = sc.nextLine();
        System.out.println("지금 작업중입니다.");

        text.setContent(context);
        System.out.println("성공");
    }

    public void readMsgInChannel(UUID chId, JCFUserService us) {
        List<Message> flag = msgs.stream().filter(e -> e.getSendChannel().equals(chId)).toList();

        if (flag.isEmpty()) {
            System.out.println("해당 서버에 보낸 메시지가 없어요.");
        }

        flag.forEach(m -> {
            System.out.println("보낸 사용자: " + us.getUserName(m.getSendUserId()));
            System.out.println("보낸 내용: " + m.getContent());
            System.out.println("===============");
        });
    }

    public void readMsgForUser(User user, JCFChannelService cs, JCFUserService us) {
       List<Message> msgList = msgs.stream().filter(e -> e.getSendUserId().equals(user.getId())).toList();

        if(msgList.isEmpty()) {
            System.out.println("아쉽지만 아무것도 없네요!");
            return;
        }

        System.out.println("당신이 보낸 메시지는 아래와 같습니다.");
        for(Message msg : msgList) {
            System.out.println("ID : " + msg.getId());
            System.out.println("보낸 사용자 : " + us.getUserName(msg.getSendUserId()));
            System.out.println("보낸 채널명 : " + cs.readServer(msg.getSendChannel()));
            System.out.println("내용 : " + msg.getContent());
            System.out.println("===============");
        }
    }

    public void deleteMsg(Scanner sc, User user, JCFChannelService cs, JCFUserService us) {
        readMsgForUser(user, cs, us);
        System.out.println("어떤 메시지를 삭제하고 싶나요? ID로 입력해주세요.");
        String id = sc.nextLine();

        UUID targetId = UUID.fromString(id);

        Message msg = msgs.stream().filter(e -> e.getId().equals(targetId)).findFirst().orElse(null);
        if(msg == null) {System.out.println("해당 ID를 가진 메시지를 찾을 수 없습니다.");return;}
        System.out.println("해당 메시지를 삭제합니까? (Y or any Key)");
        String ins = sc.nextLine();
        if(ins.equalsIgnoreCase("Y")) {
            msgs.remove(msg);System.out.println("성공!");
        } else {
            System.out.println("초기로 돌아갑니다");
        }
    }
}
