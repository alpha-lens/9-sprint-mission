package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateChannelDto;
import com.sprint.mission.discodeit.dto.FindChannelDto;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public boolean isPresent(UUID id) {
        return channelRepository.isPresentChannel(id);
    }

    @Override
    public ResponseChannelDto create(String type, String name, UUID userId) {
        String createUserName = userRepository.userIdToName(userId);

        if(type.equalsIgnoreCase("public") || type.equals("1")) {
            return channelRepository.save(new CreateChannelDto(name, createUserName, userId, ChannelType.PUBLIC));
        } else if (type.equalsIgnoreCase("private") || type.equals("2")) {
            return channelRepository.save(new CreateChannelDto(name, createUserName, userId, ChannelType.PRIVATE));
        }

        return null;
    }

    @Override
    public FindChannelDto find(UUID channelId, UUID userId) {
        ResponseChannelDto channelInfo = channelRepository.findChannel(channelId, userId);
        ChannelType channelType = channelRepository.getChannelType(channelId);
        Instant lastMessageTime = null;
        try {
            lastMessageTime = messageRepository.getLastMessageInChannel(channelId);
        } catch (Exception ignore){}

        return new FindChannelDto(channelInfo, channelType, lastMessageTime);
    }

    @Override
    public List<ResponseChannelDto> findAllPrivateChannel(UUID userId) {
        return channelRepository.findAllPrivateChannel(userId);
    }

    @Override
    public List<ResponseChannelDto> findAll(UUID userId) {
        return channelRepository.findAllChannel(userId);
    }

    @Override
    public ResponseChannelDto update(UpdateChannelDto requestDto) {
        return channelRepository.save(requestDto);
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.deleteChannel(id);
    }

    @Override
    public void deleteAll(String name) {
        channelRepository.deleteAllChannel(name);
    }

    @Override
    public boolean includePrivateChannel(String channelName, String userName, UUID userId) {
        channelRepository.includePrivateChannel(channelName, userName, userId);
        return true;
    }

    @Override
    public void excludePrivateChannel(String channelName, String userName) {
        channelRepository.excludePrivateChannel(channelName, userName);
    }

    @Override
    public boolean isCreatePrivateChannel(String userName) {
        return channelRepository.isCreatePrivateChannel(userName);
    }

    @Override
    public boolean findChannelCreator(UUID id, String userName) {
        return channelRepository.findChannelCreator(id, userName);
    }
}
