package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.app.JavaApplication;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.text.SimpleDateFormat;
import java.util.*;

public class JCFMessageService implements MessageService
{
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Scanner sc = JavaApplication.scanner();
    JCFUserRepository userRepository = JCFUserRepository.getInstance();
    JCFChannelRepository channelRepository = JCFChannelRepository.getInstance();
    JCFMessageRepository messageRepository = JCFMessageRepository.getInstance();

    private JCFMessageService() {
    }

    private static class Holder {
        private static final JCFMessageService INSTANCE = new JCFMessageService();
    }

    public static JCFMessageService getInstance() {return JCFMessageService.Holder.INSTANCE;}

    @Override
    public void createMessage() {
        System.out.println("누가 보내는 메시지인가요?");
        String senderUserName = sc.nextLine();
        if(userRepository.userNameToId(senderUserName) == null){
            System.err.println("존재하지 않는 사용자입니다.");
            return;
        };

        System.out.println("어디로 보내는 메시지인가요?");
        String sendeeChannelName = sc.nextLine();

        if(channelRepository.isPresentChannel(sendeeChannelName)) {
            System.out.println("존재하지 않는 채널입니다.");
            return;
        }

        while(true) {
            System.out.println("현재 메시지를 보낼 채널은 " + sendeeChannelName + "입니다.");
            System.out.println("현재 메시지를 보낼 사람은 " + senderUserName + "입니다.");
            System.out.println("무어라 보내고 싶으신가요?");
            String text = sc.nextLine();

            System.out.println("현재 채널에 '" + text + "'라고 보내려 합니다.");
            System.out.println("보내시려면 1을, 아니라면 0을 입력해주세요");
            String n = sc.nextLine();

            if (Objects.equals(n, "0")) {
                System.out.println("처음으로 돌아갑니다.");
                return;
            } else if (Objects.equals(n, "1")) {
                if(messageRepository.createMessage(text, sendeeChannelName, senderUserName)) {
                    System.out.println("성공.");
                }
                return;
            } else {
                System.out.println("잘못 입력했습니다. 메시지 입력 부분으로 돌아갑니다.");
            }
        }
    }

    @Override
    public void getMessageForSender(String senderName) {
        List<String> messages = messageRepository.getMessageForSender(senderName);
        if (messages.isEmpty()) {
            System.out.println("아쉽지만 아무것도 없네요!");
            return;
        }

        System.out.println("당신이 보낸 메시지는 아래와 같습니다.");
        messages.forEach(System.out::print);

    }

    @Override
    public void getMessageInChannel(String channelName) {
        List<String> messages = messageRepository.getInChannelMessage(channelName);

        if (messages.isEmpty()) {
            System.out.println("해당 채널에 보낸 메시지가 없어요.");
            return;
        }

        messages.forEach(System.out::print);
        System.out.println("총 메시지 : " + messages.size());
    }

    @Override
    public void updateMessage(String userName) {
        getMessageForSender(userName);

        System.out.println("어떤 것을 수정하고 싶나요?");
        String id = sc.nextLine();

        if (messageRepository.check(userName, UUID.fromString(id))) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String content = sc.nextLine();

        if(messageRepository.updateMessage(UUID.fromString(id), content)) {
            System.out.println("성공적으로 변경되었습니다.");
        } else {
            System.err.println("실패!");
        }
    }

    @Override
    public void deleteMessage(String userName) {
        getMessageForSender(userName);
        System.out.println("어떤 메시지를 삭제하고 싶나요? ID로 입력해주세요.");
        String id = sc.nextLine();

        if (messageRepository.check(userName, UUID.fromString(id))) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        System.out.println("해당 메시지를 삭제합니까? (Y or any Key)");
        String isDelete = sc.nextLine();

        if (isDelete.equalsIgnoreCase("Y")) {
            if(messageRepository.deleteMessage(userName, UUID.fromString(id))) {
                System.out.println("성공!");
            } else System.out.println("실패!");
        } else System.out.println("초기로 돌아갑니다");
    }
}
