package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.AttachmentType;

import java.util.UUID;

public interface BinaryContentRepository {
    boolean create(AttachmentType type, UUID id, String file);

    boolean delete(AttachmentType type, UUID id);
}
