package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    List<ReadStatusResponse> create(ReadStatusCreateRequest request);

    Instant find(UUID id);

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse update(UUID userId, UUID channelId);

    void deleteForChannel(UUID channelId);

    void deleteForUser(UUID userId);
}
