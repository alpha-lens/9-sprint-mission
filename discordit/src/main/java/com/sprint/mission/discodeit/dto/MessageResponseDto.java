package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        UUID channelId,
        UUID userId,
        List<UUID> attachmentIds,
        String createAt,
        String updateAt,
        String content
) {
}
