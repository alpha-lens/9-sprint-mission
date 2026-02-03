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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final FileChannelRepository channelRepository;
    private final FileUserRepository userRepository;
    private final UserState userState;
    private final FileMessageRepository messageRepository;

    @Override
    public boolean isPresent(String name) {
        return channelRepository.isPresentChannel(name);
    }

    @Override
    public boolean create(String type, String name) {
        String createUserName = userState.getUserName();
        UUID createUserId = userRepository.userNameToId(createUserName);

        if(type.equalsIgnoreCase("public") || type.equals("1")) {
            channelRepository.save(new Channel(name, createUserName, createUserId));
        } else if (type.equalsIgnoreCase("private") || type.equals("2")) {
            channelRepository.save(new Channel(name, createUserName, createUserId, ChannelType.PRIVATE));
        } else return false;

        return true;
    }

    @Override
    public FindChannelDto find(String name) {
        UUID channelId = channelRepository.channelNameToId(name);

        String channelInfo = channelRepository.readChannel(name);
        ChannelType channelType = channelRepository.getChannelType(name);
        Instant lastMessageTime = messageRepository.getLastMessageInChannel(channelId);

        return new FindChannelDto(channelInfo, channelType, lastMessageTime);
    }

    @Override
    public List<ResponseChannelDto> findAllPrivateChannel(String userName) {
        return channelRepository.readAllPrivateChannel(userName);
    }

    public UUID findChannelId(String channelName) {
        return channelRepository.channelNameToId(channelName);
    }

    @Override
    public List<ResponseChannelDto> findAll(String userName) {
        return channelRepository.readAllChannel(userName);
    }

    @Override
    public boolean update(String oldName, String newName) {
        return channelRepository.save(oldName, newName);
    }

    @Override
    public boolean delete(String name) {
        return channelRepository.deleteChannel(name);
    }

    @Override
    public boolean invitePrivateServer(String channelName, String userName, UUID userId) {
        channelRepository.invitePrivateServer(channelName, userName, userId);
        return true;
    }
}
