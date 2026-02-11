package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContentType;

public record RequestCreateBinaryContentDto(
        BinaryContentType type, String filename, byte[] bytes
) {
}
