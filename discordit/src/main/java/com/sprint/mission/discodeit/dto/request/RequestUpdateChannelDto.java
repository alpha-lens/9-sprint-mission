package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record RequestUpdateChannelDto(UUID id, String newName) {
}
