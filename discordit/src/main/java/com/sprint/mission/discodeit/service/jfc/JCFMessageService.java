package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// TODO: 채널 메시지 조회헤엇
/// 구분자, 전체 메시지 개수 넣어주기

public class JCFMessageService implements MessageService {
    private final Map<UUID, List<Message>> channelIdMessageMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<Message>> userIdMessageMap = new ConcurrentHashMap<>();

    /// 20260115 멘토링 때, 싱글톤 패턴이 적용되지 않았다고 해서 넣어봄
    private JCFMessageService() {
    }

    private static class Holder {
        private static final JCFMessageService INSTANCE = new JCFMessageService();
    }

    public static JCFMessageService getInstance() {
        return JCFMessageService.Holder.INSTANCE;
    }

    @Override
    public void createMessage(Scanner sc, Channel channel, User user) {
        while(true) {
            if (channel == null) return;
            System.out.println("현재 메시지를 보낼 채널은 " + channel.getName() + "입니다.");
            System.out.println("현재 메시지를 보낼 사람은 " + user.getName() + "입니다.");
            System.out.println("무어라 보내고 싶으신가요?");
            String text = sc.nextLine();

            System.out.println("현재 채널에 '" + text + "'라고 보내려 합니다.");
            System.out.println("보내시려면 1을, 아니라면 0을 입력해주세요");
            String n = sc.nextLine();

            if (Objects.equals(n, "0")) {
                System.out.println("처음으로 돌아갑니다.");
                return;
            } else if (Objects.equals(n, "1")) {
                Message message = new Message(channel.getId(), user.getId(), text);
                channelIdMessageMap.computeIfAbsent(channel.getId(), m -> new ArrayList<Message>()).add(message);
                userIdMessageMap.computeIfAbsent(user.getId(), m -> new ArrayList<Message>()).add(message);

                System.out.println("성공.");
                return;
            } else {
                System.out.println("잘못 입력했습니다. 다시 메시지 입력 부분으로 돌아갑니다.");
            }
        }
    }

    /// update logic
    @Override
    public void updateMessage(Scanner sc, User user) {
        getMessageForSender(user);

        System.out.println("어떤 것을 수정하고 싶나요?");
        String id = sc.nextLine();

        Message text = userIdMessageMap.get(user.getId()).stream().filter(m -> m.getId().equals(UUID.fromString(id))).findFirst().orElse(null);

        if (text == null) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String content = sc.nextLine();
        System.out.println("지금 작업중입니다.");

        text.updateMessage(content);
        System.out.println("성공");
    }

    @Override
    public void getMessageInChannel(UUID channelId, JCFUserService userService) {
        List<Message> flag = channelIdMessageMap.get(channelId);

        if (flag.isEmpty()) {
            System.out.println("해당 채널에 보낸 메시지가 없어요.");
        }

        flag.forEach(message -> {
            System.out.println("보낸 사용자: " + userService.getUserByName(message.getSendUserId()));
            System.out.println("보낸 내용: " + message.getContent());
            System.out.println("보낸일시 : " + message.getCreateAt());
            System.out.println("수정일시 : " + message.getUpdateAt());
            System.out.println("===============");
        });
    }

    @Override
    public void getMessageForSender(User sender) {
        List<Message> messages = userIdMessageMap.get(sender.getId());
        JCFUserService userService = JCFUserService.getInstance();
        JCFChannelService channelService = JCFChannelService.getInstance();
        if (messages.isEmpty()) {
            System.out.println("아쉽지만 아무것도 없네요!");
            return;
        }

        System.out.println("당신이 보낸 메시지는 아래와 같습니다.");
        for (Message message : messages) {
            System.out.println("ID : " + message.getId());
            System.out.println("보낸 사용자 : " + userService.getUserByName(message.getSendUserId()));
            System.out.println("보낸 채널명 : " + channelService.isChannelName(message.getSendChannel()));
            System.out.println("내용 : " + message.getContent());
            System.out.println("보낸일시 : " + message.getCreateAt());
            System.out.println("수정일시 : " + message.getUpdateAt());
            System.out.println("===============");
        }
    }

    @Override
    public void deleteMessage(Scanner sc, User user) {
        getMessageForSender(user);
        System.out.println("어떤 메시지를 삭제하고 싶나요? ID로 입력해주세요.");
        String id = sc.nextLine();

        Message message = userIdMessageMap.get(user.getId()).stream().filter(e -> e.getId().equals(UUID.fromString(id))).findFirst().orElse(null);
        if (message == null) {
            System.out.println("해당 ID를 가진 메시지를 찾을 수 없습니다.");
            return;
        }
        System.out.println("해당 메시지를 삭제합니까? (Y or any Key)");
        String isDelete = sc.nextLine();
        if (isDelete.equalsIgnoreCase("Y")) {
            userIdMessageMap.get(user.getId()).remove(message);
            channelIdMessageMap.get(message.getSendChannel()).remove(message);
            System.out.println("성공!");
        } else {
            System.out.println("초기로 돌아갑니다");
        }
    }
}
