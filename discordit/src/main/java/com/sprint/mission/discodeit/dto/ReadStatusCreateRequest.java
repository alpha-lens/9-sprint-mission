package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        List<UUID> channelIds
) {
}
