package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.AttachmentType;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    boolean create(AttachmentType type, UUID id, String file);

    List<String> find(AttachmentType type, UUID id);

    boolean delete(AttachmentType type, UUID id);
}
