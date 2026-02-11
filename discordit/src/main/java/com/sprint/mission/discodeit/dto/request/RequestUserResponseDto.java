package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record RequestUserResponseDto(UUID id, String name, String userInfo, UUID profileId) {
}
