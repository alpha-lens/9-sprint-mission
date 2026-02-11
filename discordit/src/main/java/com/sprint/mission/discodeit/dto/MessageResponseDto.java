package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
        UUID messageId,
        UUID channelId,
        UUID userId,
        List<UUID> binaryContentIds,
        String createAt,
        String updateAt,
        String content
) {
}
