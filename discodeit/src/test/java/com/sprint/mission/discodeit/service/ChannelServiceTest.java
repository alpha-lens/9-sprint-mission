package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import java.util.List;
import java.util.Map;
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
public class ChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private ChannelMapper channelMapper;

  @InjectMocks
  private BasicChannelService channelService;

  @Nested
  @DisplayName("채널 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: 참여자가 정상적으로 존재할 경우, Private 채널 생성에 성공한다.")
    void createSuccessPrivateChannel() {
      // 1. Given: 테스트 환경 설정
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      List<UUID> participantIds = List.of(id1, id2);
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

      User user1 = new User("User1", "email1@test.com", "path1", null);
      User user2 = new User("User2", "email2@test.com", "path2", null);
      List<User> participants = List.of(user1, user2);

      // when
      when(userRepository.findAllById(participantIds)).thenReturn(participants);
      when(channelRepository.save(any(Channel.class))).thenAnswer(i -> i.getArguments()[0]);
      channelService.create(request);

      // 3. Then: 행위 검증
      verify(readStatusRepository).saveAll(any());
      verify(channelRepository).save(any(Channel.class));
    }

    @Test
    @DisplayName("실패: 참여자 수와 실제 사용자의 수가 맞지 않을 경우, Private 채널 생성에 실패한다.")
    void creteFailPrivateChannel() {
      // give
      UUID id1 = UUID.randomUUID();
      UUID id2 = UUID.randomUUID();
      List<UUID> participantIds = List.of(id1, id2);
      PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

      User user1 = new User("User1", "email1@test.com", "path1", null);
      List<User> participants = List.of(user1);

      // when
      when(userRepository.findAllById(participantIds)).thenReturn(participants);

      // then
      UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
        channelService.create(request);
      });

      assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
      assertEquals(
          Map.of("요청한 사용자 수", request.participantIds().size(), "실제 사용자 수", participants.size()),
          ex.getDetails());
    }
  }

  @Nested
  @DisplayName("채널 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: Public 채널의 이름, 설명을 수정할 수 있다.")
    void updateSuccessPublicChannel() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", "설명");
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 이름", "새로운 설명");

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

      // when
      channelService.update(channelId, request);

      // then
      assertEquals("새로운 이름", channel.getName());
      assertEquals("새로운 설명", channel.getDescription());
      verify(channelRepository).save(channel);
    }

    @Test
    @DisplayName("실패: Private 채널의 이름, 설명을 수정할 수 있다.")
    void updateFailPrivateChannel() {
      // given
      UUID channelId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PRIVATE, "이름", "설명");
      PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("새로운 이름", "새로운 설명");

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

      // when & then
      PrivateChannelUpdateException ex = assertThrows(PrivateChannelUpdateException.class,
          () -> channelService.update(channelId, request));

      assertEquals(ErrorCode.PRIVATE_CHANNEL_UPDATE, ex.getErrorCode());
      assertEquals(Map.of("channelId", channelId), ex.getDetails());
    }
  }

  @Nested
  @DisplayName("채널 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 채널이 존재할 경우 삭제할 수 있다")
    void DeleteSuccess() {
      UUID channelId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", "설명");

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      channelService.delete(channelId);

      verify(messageRepository).deleteAllByChannelId(any());
      verify(readStatusRepository).deleteAllByChannel_Id(any());
      verify(channelRepository).deleteById(any());
    }

    @Test
    @DisplayName("실패: 채널이 존재하지 않을 경우 ChannelNotFoundException 예외를 반환한다.")
    void DeleteFail() {
      UUID channelId = UUID.randomUUID();

      when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

      ChannelNotFoundException ex = assertThrows(ChannelNotFoundException.class,
          () -> channelService.delete(channelId));

      assertEquals(ErrorCode.CHANNEL_NOT_FOUND, ex.getErrorCode());
      assertEquals(Map.of("channelId", channelId), ex.getDetails());
    }
  }
}
