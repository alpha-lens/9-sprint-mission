package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    User user = request.user();
    Channel channel = request.channel();

    if (!userRepository.existsById(user.getId())) {
      throw new NoSuchElementException("Username " + user.getUsername() + " does not exist");
    }
    if (!channelRepository.existsById(channel.getId())) {
      throw new NoSuchElementException(
          "Channel with name " + channel.getName() + " does not exist");
    }

    return readStatusMapper.toDto(readStatusRepository.findAllByUser_Id(user.getId()).stream()
        .filter(readStatus -> readStatus.getChannelId().equals(channel))
        .findFirst()
        .orElseGet(
            () -> {
              Instant lastReadAt = request.lastReadAt();
              ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
              return readStatusRepository.save(readStatus);
            }
        ));
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    return readStatusMapper.toDto(readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found")));
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatusMapper::toDto).toList();
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("ReadStatus with id " + readStatusId + " not found"));
    readStatus.update(newLastReadAt);
    return readStatusMapper.toDto(readStatusRepository.save(readStatus));
  }

  @Override
  @Transactional
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
