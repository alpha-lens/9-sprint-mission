package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record MessageCreateRequest(
    @Schema(description = "메시지 내용")
    String content,

    @Schema(description = "채널")
    UUID channelId,

    @Schema(description = "작성자")
    UUID authorId
) {

}
