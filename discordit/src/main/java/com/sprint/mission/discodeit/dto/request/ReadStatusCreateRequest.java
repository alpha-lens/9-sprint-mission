package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ReadStatusCreateRequest(
    @Schema(description = "사용자 ID")
    User user,
    @Schema(description = "채널 ID")
    Channel channel,
    @Schema(description = "읽음 시간")
    Instant lastReadAt
) {

}
