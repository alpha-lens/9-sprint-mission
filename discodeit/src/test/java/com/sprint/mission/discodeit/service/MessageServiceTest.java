package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.ArrayList;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private PageResponseMapper pageResponseMapper;

  @InjectMocks
  private BasicMessageService messageService;

  @Nested
  @DisplayName("메시지 생성 테스트")
  class CreateTest {

    @Test
    @DisplayName("성공: 사용자는 채널에 첨부파일을 포함한 메시지를 보낼 수 있다.")
    void CreateSuccessMessage() {
      // given
      UUID channelId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", null);
      User user = new User("이름", "e@mail.com", "pass", null);
      MessageCreateRequest request = new MessageCreateRequest("메시지", channelId, authorId);
      List<MultipartFile> attachments = new ArrayList<>();
      attachments.add(new MockMultipartFile(
          "이미지", "avatar.png", "image/png", new byte[]{}
      ));
      attachments.add(new MockMultipartFile(
          "이미지1", "avatar1.png", "image/png", new byte[]{}
      ));
      UserDto userDto = new UserDto(authorId, user.getUsername(), user.getEmail(), null, true);
      List<BinaryContentDto> binaryContentDto = List.of(
          new BinaryContentDto(UUID.randomUUID(), "avatar.png", 3324L, "image/png"),
          new BinaryContentDto(UUID.randomUUID(), "avatar1.png", 3324L, "image/png")
      );
      MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "메시지",
          channelId, userDto, binaryContentDto);

      // when
      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      when(userRepository.findById(authorId)).thenReturn(Optional.of(user));
      when(binaryContentRepository.save(any(
          BinaryContent.class))).thenAnswer(invocation -> invocation.getArgument(0));
      when(messageRepository.save(any(Message.class))).thenAnswer(
          invocation -> invocation.getArgument(0));
      when(messageMapper.toDto(any(), any())).thenReturn(messageDto);
      MessageDto result = messageService.create(request, attachments);

      // then
      assertNotNull(result);
      verify(binaryContentRepository, times(2)).save(any());
      verify(binaryContentStorage, times(2)).put(any(), any());
      verify(messageRepository).save(any());
    }

    @Test
    @DisplayName("실패: 채널이 존재하지 않을 경우 ChannelNotFoundException을 발생시킨다")
    void CreateFailMessageForChannelNotFoundException() {
      UUID channelId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      MessageCreateRequest request = new MessageCreateRequest("메시지", channelId, authorId);

      when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

      ChannelNotFoundException ex = assertThrows(ChannelNotFoundException.class,
          () -> messageService.create(request, null));

      assertEquals(Map.of("channelId", channelId), ex.getDetails());
      assertEquals(ErrorCode.CHANNEL_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("실패: 작성자가 존재하지 않을 경우 ChannelNotFoundException을 발생시킨다")
    void CreateFailMessageForUserNotFoundException() {
      UUID channelId = UUID.randomUUID();
      UUID authorId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, "이름", "설명");
      MessageCreateRequest request = new MessageCreateRequest("메시지", channelId, authorId);

      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));

      UserNotFoundException ex = assertThrows(UserNotFoundException.class,
          () -> messageService.create(request, null));

      assertEquals(Map.of("userId", authorId), ex.getDetails());
      assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
  }

  @Nested
  @DisplayName("메시지 수정 테스트")
  class UpdateTest {

    @Test
    @DisplayName("성공: 메시지를 수정할 수 있다.")
    void UpdateSuccess() {
      // given
      UUID messageId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, null, null);
      User user = new User("이름", "e@ma.il", "ps", null);
      Message message = new Message("메시지", channel, user, null);
      MessageUpdateRequest request = new MessageUpdateRequest("새 메시지");

      // when
      when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
      messageService.update(messageId, request);

      // then
      assertEquals("새 메시지", message.getContent());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 메시지는 수정할 수 없다.")
    void UpdateFail() {
      // given
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest request = new MessageUpdateRequest("새 메시지");

      // when
      when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

      // then
      MessageNotFoundException ex = assertThrows(MessageNotFoundException.class,
          () -> messageService.update(messageId, request));
      assertEquals(ErrorCode.MESSAGE_NOT_FOUND, ex.getErrorCode());
      assertEquals(Map.of("messageId", messageId), ex.getDetails());
    }
  }

  @Nested
  @DisplayName("메시지 삭제 테스트")
  class DeleteTest {

    @Test
    @DisplayName("성공: 메시지가 존재할 경우, 메시지를 삭제할 수 있다")
    void DeleteSuccess() {
      // given
      UUID messageId = UUID.randomUUID();
      Channel channel = new Channel(ChannelType.PUBLIC, null, null);
      User user = new User("이름", "e@ma.il", "ps", null);
      Message message = new Message("메시지", channel, user, null);

      // when
      when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
      messageService.delete(messageId);

      // then
      verify(messageRepository).findById(messageId);
      verify(messageRepository).delete(message);
    }

    @Test
    @DisplayName("실패: 메시지가 존재하지 않을 경우, MessageNotFoundException을 발생시킨다.")
    void DeleteFail() {
      // given
      UUID messageId = UUID.randomUUID();

      // when&then
      MessageNotFoundException ex = assertThrows(MessageNotFoundException.class,
          () -> messageService.delete(messageId));

      assertEquals(ErrorCode.MESSAGE_NOT_FOUND, ex.getErrorCode());
      assertEquals(Map.of("messageId", messageId), ex.getDetails());
    }
  }
}
