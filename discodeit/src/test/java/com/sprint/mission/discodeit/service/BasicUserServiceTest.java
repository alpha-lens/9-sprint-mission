package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  @Nested
  @DisplayName("사용자 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: 사용자명, 이메일이 중복되지 않을 경우 모든 저장 로직이 실행된다")
    void CreateSuccess() {
      // given
      byte[] bytes = new byte[0];
      UserCreateRequest request = new UserCreateRequest("test", "test@gmail.com", "pass");
      BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("profile.png",
          "image/png", bytes);

      when(userRepository.existsByEmail(anyString())).thenReturn(false);
      when(userRepository.existsByUsername(anyString())).thenReturn(false);
      when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      // when
      userService.create(request, Optional.of(profileRequest));

      // then
      verify(binaryContentRepository).save(any());
      verify(binaryContentStorage).put(any(), any());
      verify(userRepository).save(any());
      verify(userStatusRepository).save(any());
    }

    @Test
    @DisplayName("실패: 사용자명이 중복될 경우 UserAlreadyExistsException을 발생시킨다.")
    void CreateFailDuplicateUsername() {
      // given
      byte[] bytes = new byte[0];
      UserCreateRequest request = new UserCreateRequest("test", "duplicate@gmail.com", "pass");
      BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("profile.png",
          "image/png", bytes);

      when(userRepository.existsByEmail(anyString())).thenReturn(true);

      // when&then
      UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
          () -> userService.create(request, Optional.of(profileRequest)));

      assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }
  }

  @Nested
  @DisplayName("사용자 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 사용자와 연관된 프로필 이미지, 상태 정보가 모두 삭제된다")
    void DeleteSuccess() {
      // given
      UUID userId = UUID.randomUUID();
      BinaryContent profile = new BinaryContent("old.png", 3324L, "image/png");
      User user = new User("test", "test@email.com", "pass", profile);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      // when
      userService.delete(userId);

      verify(binaryContentRepository).deleteById(profile.getId());
      verify(userStatusRepository).deleteByUser_Id(userId);
      verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("실패: 존재하지 않는 ID 삭제 시 UserNotFoundException 발생")
    void DeleteFail() {
      // given
      UUID userId = UUID.randomUUID();
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      // when&then
      assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
    }
  }

  @Nested
  @DisplayName("사용자 삭제 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: 변경하고자 하는 사용자명/이메일이 중복되지 않으면 변경할 수 있다.")
    void UpdateSuccess() {
      // given
      byte[] bytes = new byte[0];
      UUID userId = UUID.randomUUID();
      BinaryContent oldProfile = new BinaryContent("old.png", 10L, "image/png");
      User existingUser = new User("oldName", "old@email.com", "pass", oldProfile);
      UserUpdateRequest request = new UserUpdateRequest("test", "test@gmail.com", "pass");
      BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("profile.png",
          "image/png", bytes);

      when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
      when(userRepository.existsByEmail(anyString())).thenReturn(false);
      when(userRepository.existsByUsername(anyString())).thenReturn(false);
      when(binaryContentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      // when
      userService.update(userId, request, Optional.of(profileRequest));

      // then
      verify(binaryContentRepository).deleteById(any());
      verify(binaryContentRepository).save(any());
      verify(binaryContentStorage).put(any(), any());
      verify(userRepository).save(any());
    }

    @Test
    @DisplayName("실패: 중복되는 사용자명/이메일이 있을 경우 ")
    void UpdateFail() {
      // given
      UUID userId = UUID.randomUUID();
      User user = new User("duplicate", "email@mail.com", "pass", null);
      UserUpdateRequest userUpdateRequest = new UserUpdateRequest("duplicate", "email@mail.com",
          "pass");
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(userRepository.existsByUsername(anyString())).thenReturn(true);

      // when&then
      assertThrows(UserAlreadyExistsException.class,
          () -> userService.update(userId, userUpdateRequest, Optional.empty()));
    }
  }

}
