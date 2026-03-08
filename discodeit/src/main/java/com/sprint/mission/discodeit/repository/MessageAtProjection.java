package com.sprint.mission.discodeit.repository;

import java.time.Instant;
import java.util.UUID;

public interface MessageAtProjection {

  UUID getChannelId();

  Instant getLastAt();
}
