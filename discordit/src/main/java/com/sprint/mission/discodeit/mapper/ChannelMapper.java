package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  MessageRepository messageRepository;
  ReadStatusRepository readStatusRepository;
  UserMapper userMapper;

  public ChannelDto toDto(Channel channel) {
    List<UserDto> participans = new ArrayList<>();
    readStatusRepository.findAllByChannel_Id(channel.getId()).forEach(readStatus -> {
      participans.add(userMapper.toDto(readStatus.getUser()));
    });
    List<Message> messages = messageRepository.findAllByChannelId(channel.getId());
    Instant lastMessageAt = messages.get(messages.size() - 1).getCreatedAt();
    return new ChannelDto(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        participans,
        lastMessageAt
    );
  }
}
