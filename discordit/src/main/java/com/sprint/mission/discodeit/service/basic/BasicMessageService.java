package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    Channel channel = channelRepository.findById(channelId).orElseThrow(
        () -> new NoSuchElementException("Channel with id " + channelId + " not found")
    );
    User author = userRepository.findById(authorId).orElseThrow(
        () -> new NoSuchElementException("Author with id " + authorId + " not found")
    );

    String content = messageCreateRequest.content();
    List<BinaryContent> binaryContents = new ArrayList<>();

    if (attachments != null && !attachments.isEmpty()) {
      for (MultipartFile file : attachments) {
        BinaryContent metadata = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );

        BinaryContent savedMetadata = binaryContentRepository.save(metadata);
        binaryContents.add(savedMetadata);

        try {
          binaryContentStorage.put(savedMetadata.getId(), file.getBytes());
        } catch (IOException e) {
          throw new RuntimeException("Failed file saved: " + file.getOriginalFilename(), e);
        }
      }
    }

    Message message = new Message(
        content,
        channel,
        author,
        binaryContents
    );

    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public MessageDto find(UUID messageId) {
    return messageMapper.toDto(messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found")));
  }

  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .map(messageMapper::toDto).toList();
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    return messageMapper.toDto(message.update(newContent));
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    messageRepository.deleteById(messageId);
  }

  @Override
  public PageResponse<Message> findAllByChannelId(UUID channelId, Pageable pageable) {
    Page<Message> messages = messageRepository.findAllByChannelId(channelId, pageable);
    Page<MessageDto> dtoPage = messages.map(messageMapper::toDto);
    return pageResponseMapper.fromPage(dtoPage);
  }

  @Override
  public PageResponse<MessageDto> findAllSliceByChannelId(UUID channelId, Pageable pageable) {
    Slice<Message> messageSlice = messageRepository.findAllByChannelId(channelId, pageable);
    Slice<MessageDto> dtoSlice = messageSlice.map(messageMapper::toDto);

    return pageResponseMapper.fromSlice(dtoSlice);
  }
}
