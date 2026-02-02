package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.AttachmentType;

import java.util.List;
import java.util.UUID;

public record BinaryContentDto(AttachmentType type, UUID id, String filename) {
}
