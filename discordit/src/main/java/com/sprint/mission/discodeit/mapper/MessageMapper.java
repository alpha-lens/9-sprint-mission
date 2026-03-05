package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  BinaryContentMapper binaryContentMapper;
  UserMapper userMapper;

  public MessageDto toDto(Message message) {
    List<BinaryContentDto> binaryContentDtos = new ArrayList<>();
    message.getAttachments().forEach(
        binaryContent -> {
          binaryContentDtos.add(binaryContentMapper.toDto(binaryContent));
        }
    );

    return new MessageDto(
        message.getId(),
        message.getContent(),
        message.getChannel().getId(),
        userMapper.toDto(message.getAuthor()),
        binaryContentDtos
    );
  }
}
