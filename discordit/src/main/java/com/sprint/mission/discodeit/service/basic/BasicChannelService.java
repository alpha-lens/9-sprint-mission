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

  /* 이 부분은 AI 도움을 받았는데 아직 학습이 필요합니다.
   * 1. 왜 모든 정보를 가져와서 처리하는지 모르겠습니다. (저는 쿼리문에서 필터링해서 가져오려고 생각했었는데, AI는 모든걸 가져와서 필터링했습니다.)
   * 2. 메시지 시간 처리 로직도 좀 더 배워야 합니다.
   * 3. 채널 ID와 UserDto로 묶는 부분은, 본래 Channel과 UserDto로 묶었던 것인데, AI는 ID로 처리했습니다. 이유를 모르겠습니다.
   * 4. obj[0], obj[1]이 마음에 안 들어서 바꾸고 싶은데... 실패했습니다.
   * */
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // 1. [DB 필터링] 내가 볼 수 있는 채널만 조회
    List<Channel> channels = channelRepository.findAccessibleChannelsByUserId(userId);
    if (channels.isEmpty()) {
      return List.of();
    }

    List<UUID> channelIds = channels.stream().map(Channel::getId).toList();

    // 2. [Batch Query] 마지막 메시지 시간들을 Map으로 변환 (Projection 활용)
    Map<UUID, Instant> lastMessageMap = messageRepository.findLastMessageAtByChannelIds(channelIds)
        .stream()
        .collect(Collectors.toMap(
            MessageAtProjection::getChannelId,
            MessageAtProjection::getLastAt
        ));

    // 3. [Batch Query] Private 채널 참여자 목록 조회
    // 모든 ReadStatus를 가져오되, 필요한 채널 ID들에 대해서만 필터링
    Map<UUID, List<UserDto>> participantsMap = readStatusRepository.findAllByChannelIdsWithUser(
            channelIds)
        .stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(),
            Collectors.mapping(rs -> userMapper.toDto(rs.getUser()), Collectors.toList())
        ));

    // 4. DTO 조립
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
