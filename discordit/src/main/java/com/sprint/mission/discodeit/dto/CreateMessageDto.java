package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record CreateMessageDto(
        String text,
        UUID channelId,
        UUID userId,
        List<UUID> binaryContentIds
) {
}
