package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAtProjection;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final ChannelMapper channelMapper;

  @Override
  @Transactional
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    return channelMapper.toDto(channelRepository.save(channel), List.of(), Instant.MIN);
  }

  @Override
  @Transactional
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    List<User> participants = userRepository.findAllById(request.participantIds());
    if (participants.size() != request.participantIds().size()) {
      throw new IllegalArgumentException(
          request.participantIds().size() + " is not equal to " + request.participantIds().size());
    }

    List<UserDto> participantsDto = participants.stream().map(userMapper::toDto).toList();

    List<ReadStatus> readStatusList = participants.stream()
        .map(user -> new ReadStatus(user, createdChannel, channel.getCreatedAt()))
        .toList();

    readStatusRepository.saveAll(readStatusList);

    return channelMapper.toDto(channelRepository.save(channel), participantsDto, Instant.MIN);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new NoSuchElementException("Channel not found: " + channelId));

    Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channelId)
        .orElse(channel.getCreatedAt());

    List<UserDto> participants = readStatusRepository.findAllByChannel_Id(channelId).stream()
        .map(rs -> userMapper.toDto(rs.getUser()))
        .toList();

    return channelMapper.toDto(channel, participants, lastMessageAt);
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> channels = channelRepository.findAccessibleChannelsByUserId(userId);
    if (channels.isEmpty()) {
      return List.of();
    }

    List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

    Map<UUID, Instant> lastMessageMap = messageRepository.findLastMessageAtByChannelIds(channelIds)
        .stream()
        .collect(Collectors.toMap(
            MessageAtProjection::getChannelId,
            MessageAtProjection::getLastAt
        ));

    Map<UUID, List<UserDto>> participantsMap = readStatusRepository.findAllByChannelIdsWithUser(
            channelIds)
        .stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(),
            Collectors.mapping(rs -> userMapper.toDto(rs.getUser()), Collectors.toList())
        ));

    return channels.stream()
        .map(channel -> {
          UUID id = channel.getId();
          List<UserDto> participants = (channel.getType() == ChannelType.PRIVATE)
              ? participantsMap.getOrDefault(id, List.of())
              : List.of();

          return channelMapper.toDto(
              channel,
              participants,
              lastMessageMap.getOrDefault(id, channel.getCreatedAt())
          );
        })
        .toList();
  }

  @Override
  @Transactional
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(newName, newDescription);
    Instant lastMessageAt = messageRepository.findLastMessageAtByChannelId(channelId)
        .orElse(channel.getCreatedAt());
    return channelMapper.toDto(channelRepository.save(channel), List.of(), lastMessageAt);
  }

  @Override
  @Transactional
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());

    channelRepository.deleteById(channelId);
  }
}
