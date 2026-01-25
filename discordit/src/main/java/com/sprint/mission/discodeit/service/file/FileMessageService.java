package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.app.JavaApplication;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileMessageService implements MessageService {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Scanner sc = JavaApplication.scanner();
    FileUserRepository userRepository = FileUserRepository.getInstance();
    FileChannelRepository channelRepository = FileChannelRepository.getInstance();
    FileMessageRepository messageRepository = FileMessageRepository.getInstance();

    private FileMessageService() {
    }

    private static class Holder {
        private static final FileMessageService INSTANCE = new FileMessageService();
    }

    public static FileMessageService getInstance() {return FileMessageService.Holder.INSTANCE;}

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

        if(!channelRepository.check(sendeeChannelName)) {
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
        List<String> messages = messageRepository.readInChannelMessage(senderName);
        if (messages.isEmpty()) {
            System.out.println("아쉽지만 아무것도 없네요!");
            return;
        }

        System.out.println("당신이 보낸 메시지는 아래와 같습니다.");
        for (Message message : messages) {
            System.out.println("ID : " + message.getId());
            System.out.println("보낸 채널명 : " + channelService.isChannelName(message.getSendChannel()));
            System.out.println("내용 : " + message.getContent());
            System.out.println("보낸일시 : " + sdf.format(new Date(message.getCreateAt())));
            System.out.println("수정일시 : " + sdf.format(new Date(message.getUpdateAt())));
            System.out.println("====================");
        }
    }

    @Override
    public void getMessageInChannel(UUID channelId) {
        FileUserService userService = FileUserService.getInstance();
        List<Message> flag = channelIdMessageMap.getOrDefault(channelId, null);

        if (flag.isEmpty()) {
            System.out.println("해당 채널에 보낸 메시지가 없어요.");
            return;
        }

        flag.forEach(message -> {
            System.out.println("보낸 사용자: " + userService.getUserByName(message.getSenderUserId()));
            System.out.println("보낸 내용: " + message.getContent());
            System.out.println("보낸일시 : " + sdf.format(new Date(message.getCreateAt())));
            System.out.println("수정일시 : " + sdf.format(new Date(message.getUpdateAt())));
            System.out.println("====================");
        });
    }

    @Override
    public void updateMessage(String userName) {
        getMessageForSender(userName);

        System.out.println("어떤 것을 수정하고 싶나요?");
        String id = sc.nextLine();

        Message text = userIdMessageMap.get(userName.getId()).stream().filter(m -> m.getId().equals(UUID.fromString(id))).findFirst().orElse(null);

        if (text == null) {
            System.out.println("실패. 해당 ID를 찾지 못했습니다.");
            return;
        }

        Path path = resolvePath(text.getId());
        Message message = null;

        if(Files.exists(path)) {
            try(FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)){
                message = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("무슨 내용으로 수정하고 싶나요?");
        String content = sc.nextLine();
        System.out.println("지금 작업중입니다.");

        text.updateMessage(content);

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(text);
            System.out.println("성공");
        } catch (IOException e) {
            throw new RuntimeException(e);
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

        Path path = resolvePath(UUID.fromString(id));

        if (isDelete.equalsIgnoreCase("Y")) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userIdMessageMap.get(user.getId()).remove(message);
            channelIdMessageMap.get(message.getSendChannel()).remove(message);
            System.out.println("성공!");
        } else {
            System.out.println("초기로 돌아갑니다");
        }
    }
}
