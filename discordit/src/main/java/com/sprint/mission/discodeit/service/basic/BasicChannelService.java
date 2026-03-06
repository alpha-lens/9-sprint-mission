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
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
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
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

//    Instant lastMessageAt = messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel).get()
//        .getCreatedAt();
//    List<UserDto> participants = readStatusRepository.findAllByChannel_Id(channelId).stream().map(
//        ReadStatus::getUser
//    ).map(userMapper::toDto).toList();

//    return channelMapper.toDto(channel, participants, lastMessageAt);
    return null;
  }

  /* 이 부분은 AI 도움을 받았는데 아직 학습이 필요합니다.
   * 1. 왜 모든 정보를 가져와서 처리하는지 모르겠습니다. (저는 쿼리문에서 필터링해서 가져오려고 생각했었는데, AI는 모든걸 가져와서 필터링했습니다.)
   * 2. 메시지 시간 처리 로직도 좀 더 배워야 합니다.
   * 3. 채널 ID와 UserDto로 묶는 부분은, 본래 Channel과 UserDto로 묶었던 것인데, AI는 ID로 처리했습니다. 이유를 모르겠습니다.
   * 4. obj[0], obj[1]이 마음에 안 들어서 바꾸고 싶은데... 실패했습니다.
   * */
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    // 쿼리 1: 모든 채널 정보 가져오기
    List<Channel> allChannels = channelRepository.findAll();
    List<UUID> allChannelIds = allChannels.stream().map(Channel::getId).toList();

    // 쿼리 2: 메시지 시간 한꺼번에 (Batch)
    Map<UUID, Instant> lastMessageMap = messageRepository.findLastMessageAtByChannelIds(
            allChannelIds)
        .stream().collect(Collectors.toMap(
            obj -> (UUID) obj[0],
            obj -> (Instant) obj[1]));

    // 쿼리 3: 모든 ReadStatus(유저 포함) 한꺼번에 (Batch Fetch Join)
    // 여기서 userId 조건 없이 채널 ID들로 긁어오면,
    // "내가 구독한 채널"인지 여부도 이 데이터 안에서 판단 가능합니다.
    List<ReadStatus> allReadStatuses = readStatusRepository.findAllByChannelIdsWithUser(
        allChannelIds);

    // 메모리에서 데이터 가공 (Java 상에서 처리)
    Map<UUID, List<UserDto>> channelUsersMap = allReadStatuses.stream()
        .collect(Collectors.groupingBy(
            rs -> rs.getChannel().getId(), // Key: 채널 ID
            Collectors.mapping(            // Value: ReadStatus를 UserDto로 변환해서 리스트로 수집
                rs -> userMapper.toDto(rs.getUser()),
                Collectors.toList()
            )
        ));

    // 내가 구독한 채널 ID 목록도 DB 가지 말고 여기서 추출
    Set<UUID> mySubscribedIds = allReadStatuses.stream()
        .filter(rs -> rs.getUser().getId().equals(userId))
        .map(rs -> rs.getChannel().getId())
        .collect(Collectors.toSet());

    // 5. 최종 DTO 조립 및 필터링
    return allChannels.stream()
        // 공개 채널이거나 내가 구독한 채널만 필터링
        .filter(c -> c.getType() == ChannelType.PUBLIC || mySubscribedIds.contains(c.getId()))
        .map(channel -> {
          UUID id = channel.getId();
          Instant lastAt = lastMessageMap.get(id);

          // PRIVATE 채널일 때만 유저 리스트를 넣어주고, 아니면 null 처리
          List<UserDto> users = (channel.getType() == ChannelType.PRIVATE)
              ? channelUsersMap.getOrDefault(id, Collections.emptyList())
              : null;

          return channelMapper.toDto(channel, users, lastAt);
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
//    return channelMapper.toDto(channelRepository.save(channel));
    return null;
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
