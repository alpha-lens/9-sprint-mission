package com.sprint.mission.discodeit.service.jfc;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    public void data() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("테스트용 메시지"));
        messages.add(new Message("테스트용 메시지2"));
        messages.add(new Message("테스트용 메시지3"));
    };
    @Override
    public void createMessage(Scanner sc, List<Message> messages){
        System.out.println("보낼 메시지를 입력해주세요");
        String msg = sc.nextLine();
        System.out.println("해당 메시지를 전송하시겠습니까? 보내려 하면 Y를 눌러주세요.");
        String isSend = sc.nextLine();
        if(isSend.toLowerCase().equals("y")) {
            messages.add(new Message(msg));
            System.out.println("완료");
        }
    };
    @Override
    public void updateMessage(Scanner sc, Message message){
        System.out.println("현재 메시지를 무엇으로 변경하겠습니까?");
        String msg = sc.nextLine();
        System.out.println("변경하겠습니까? (변경하려면 Y)");
        String isSend = sc.nextLine();
        if(isSend.toLowerCase().equals("y")) {
            System.out.println("완료");
            message.editMessage(msg);
        }
    };
    @Override
    public void getMessageName(Message user){

    };
    @Override
    public void getAllMessageName(List<Message> users){

    };
    @Override
    public void deleteMessage(Scanner sc, List<Message> messages){

    };
}
