package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
  private final BinaryContentMapper binaryContentMapper;
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

    List<BinaryContentDto> binaryContentDtos = new ArrayList<>();
    message.getAttachments().forEach(
        binaryContent -> {
          binaryContentDtos.add(binaryContentMapper.toDto(binaryContent));
        }
    );

    return messageMapper.toDto(messageRepository.save(message), binaryContentDtos);
  }

  @Override
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    List<BinaryContentDto> binaryContentDtos = new ArrayList<>();
    message.getAttachments().forEach(
        binaryContent -> {
          binaryContentDtos.add(binaryContentMapper.toDto(binaryContent));
        }
    );

    return messageMapper.toDto(message, binaryContentDtos);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    List<BinaryContentDto> binaryContentDtos = new ArrayList<>();
    message.getAttachments()
        .forEach(binaryContent -> binaryContentDtos.add(binaryContentMapper.toDto(binaryContent)));

    return messageMapper.toDto(message.update(newContent), binaryContentDtos);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    messageRepository.deleteById(messageId);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    Slice<Message> messages = messageRepository.findAllByChannelId(channelId, cursor, pageable);

    Slice<MessageDto> dtoPage = messages.map(this::convertToDto);

    Instant nextCursor = null;
    if (messages.hasNext() && !messages.getContent().isEmpty()) {
      nextCursor = messages.getContent().get(messages.getContent().size() - 1).getCreatedAt();
    }

    return pageResponseMapper.fromSlice(dtoPage, nextCursor);
  }

  private MessageDto convertToDto(Message message) {
    List<BinaryContentDto> binaryContentDtos = message.getAttachments().stream()
        .map(binaryContentMapper::toDto)
        .toList();
    return messageMapper.toDto(message, binaryContentDtos);
  }
}
