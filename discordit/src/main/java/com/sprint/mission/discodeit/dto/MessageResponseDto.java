package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record MessageResponseDto(
        UUID id,
        UUID channelId,
        UUID userId,
        String createAt,
        String updateAt,
        String content
) {
}
