package com.sprint.mission.discodeit.dto.apiresponse;

import java.util.List;
import java.util.UUID;

public record ResponseCreateMessage(
        UUID messageId,
        List<UUID> binaryContentIds,
        String content
) {
}
