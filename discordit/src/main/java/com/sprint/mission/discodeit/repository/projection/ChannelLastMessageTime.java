package com.sprint.mission.discodeit.repository.projection;

import java.time.Instant;
import java.util.UUID;

public interface ChannelLastMessageTime {

  UUID getChannelId();

  Instant getLastMessageAt();
}
