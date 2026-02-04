package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.AttachmentType;

import java.util.UUID;

public record CreateBinaryContentDto(
        AttachmentType type, String filename, byte[] bytes
) {
}
