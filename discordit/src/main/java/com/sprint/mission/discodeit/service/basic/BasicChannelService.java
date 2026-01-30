package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.FindChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final Scanner scanner;
    private final FileChannelRepository channelRepository;
    private final FileUserRepository userRepository;
    private final UserState userState;
    private final FileMessageRepository messageRepository;

    @Override
    public void createChannel() {
        System.out.println("사용하려는 채널명이 무엇인가요?");
        String name = scanner.nextLine().trim();

        if (channelRepository.isPresentChannel(name)) {
            System.out.println("이미 존재하는 채널명이에요!");
            return;
        }

        String createUserName = userState.getUserName();
        UUID createUserId = userRepository.userNameToId(createUserName);

        System.out.println("해당 채널의 성격을 알려주세요. (숫자 혹은 뒤의 영어를 입력해주시면 됩니다.)");
        System.out.println("1. PUBLIC");
        System.out.println("2. PRIVATE");
        String type = scanner.nextLine().trim();

        if(type.equalsIgnoreCase("public") || type.equals("1")) {
            channelRepository.save(new Channel(name, createUserName, createUserId));
        } else if (type.equalsIgnoreCase("private") || type.equals("2")) {
            channelRepository.save(new Channel(name, createUserName, createUserId, ChannelType.PRIVATE));
        } else {
            System.err.println("잘못된 입력값입니다. 처음으로 돌아갑니다.");
        }

        System.out.println("잘 들어갔어요!");
    }

    @Override
    public void readChannel() {
        System.out.println("검색할 채널명을 알려주세요");
        String name = scanner.nextLine().trim();

        UUID channelId = channelRepository.channelNameToId(name);

        String channelInfo = channelRepository.readChannel(name);
        ChannelType channelType = channelRepository.getChannelType(name);
        Instant lastMessageTime = messageRepository.getLastMessageInChannel(channelId);
        FindChannelDto requestChannelDto = new FindChannelDto(channelInfo, channelType, lastMessageTime);

        System.out.println(requestChannelDto.getInfo());
    }

    @Override
    public void readAllChannel() {
        List<ResponseChannelDto> allChannel = channelRepository.readAllChannel();

        if (allChannel.isEmpty()) {
            System.out.println("채널이 존재하지 않습니다.");
            return;
        }

        allChannel.forEach(System.out::println);

        System.out.println("현재 총 채널수 : " + allChannel.size());
    }

    @Override
    public void updateChannel() {
        System.out.println("변경하고자 하는 채널명을 알려주세요");
        String oldName = scanner.nextLine();

        if(!channelRepository.isPresentChannel(oldName)) {
            System.out.println("해당 채널이 존재하지 않습니다.");
            return;
        }

        System.out.println("현재 채널명 : " + oldName);
        System.out.println("무엇으로 변경하고 싶은가요? ");

        String newName = scanner.nextLine();

        if(channelRepository.save(oldName, newName))
            System.out.println("잘 변경되었어요!");
    }

//    @Override
    public void inviteUserInPrivateChannel() {
        System.out.println("현재 당신이 참여하고 있는 PRIVATE 채널은 다음과 같습니다.");

    }

    @Override
    public void deleteChannel() {
        System.out.println("[Warning!] 지금 계정을 삭제하려 하고 있습니다.");
        System.out.println("[Warning!] 만약 잘못 들어오신 경우, 0을 눌러주시기 바랍니다.");
        System.out.println("[Warning!] 계속 진행하려면 아무 숫자나 입력해주세요");

        int n = scanner.nextInt();
        scanner.nextLine();
        if (n == 0) {
            System.out.println("처음으로 돌아갑니다.");
            return;
        }

        System.out.print("삭제하려는 채널명을 알려주세요: ");
        String inputChannelName = scanner.nextLine();

        if(!channelRepository.isPresentChannel(inputChannelName)) {
            System.out.println("해당 채널을 찾을 수 없습니다.");
            return;
        }

        if (channelRepository.deleteChannel(inputChannelName)) {
            System.out.println("해당 채널이 삭제되었습니다.");
        } else {
            System.err.println("오류가 발생하여 삭제할 수 없습니다.");
        }
    }
}
