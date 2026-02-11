package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReadStatusRepository {
    List<ReadStatusResponse> create(ReadStatusCreateRequest request);

    Instant find(UUID id);

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse update(UUID userId, UUID channelId);

    boolean delete(UUID id);

    void deleteForChannel(UUID channelId);

    void deleteForUser(UUID userId);
}
