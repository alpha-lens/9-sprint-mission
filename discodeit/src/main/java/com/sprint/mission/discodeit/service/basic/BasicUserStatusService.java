package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("UserId " + userId + " does not exist");
    }
    User user = userRepository.findById(userId).orElseThrow();
    if (userStatusRepository.findByUser_Id(userId).isPresent()) {
      throw new IllegalArgumentException(
          "UserStatus with id " + user.getStatus().getId() + " already exists");
    }

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusMapper.toDto(userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found")));
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream().map(
        userStatusMapper::toDto).toList();
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with user " + userId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }
}
