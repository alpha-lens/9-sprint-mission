package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record MessageCreateRequest(
    @Schema(description = "메시지 내용")
    String content,

    @Schema(description = "채널")
    Channel channel,

    @Schema(description = "작성자")
    User author
) {

}
