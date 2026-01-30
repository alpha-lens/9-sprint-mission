package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public record CreateMessageDto(
        UUID userID,
        UUID channelId,
        String content,
        List<BinaryContent> binaryContents
) {
}
