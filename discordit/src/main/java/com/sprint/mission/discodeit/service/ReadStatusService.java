package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface ReadStatusService {
    void create(UUID userId, UUID channelId);

    void find(UUID id);

    void findAllByUserId(UUID userId);

    boolean update(UUID userId, String channelName);

    void deleteForChannel(UUID channelId);

    void deleteForUser(UUID userId);
}
