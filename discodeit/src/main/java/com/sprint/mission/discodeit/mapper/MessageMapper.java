package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {

  @Mapping(source = "message.id", target = "id")
  @Mapping(source = "message.createdAt", target = "createdAt")
  @Mapping(source = "message.updatedAt", target = "updatedAt")
  @Mapping(source = "message.content", target = "content")
  @Mapping(source = "message.channel.id", target = "channelId")
  @Mapping(source = "message.author", target = "author")
  @Mapping(source = "binaryContentDtos", target = "attachments")
  MessageDto toDto(Message message, List<BinaryContentDto> binaryContentDtos);
}