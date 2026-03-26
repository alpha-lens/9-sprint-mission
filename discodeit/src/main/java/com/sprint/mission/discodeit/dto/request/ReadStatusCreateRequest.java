package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull @NotBlank
    @Schema(description = "사용자 ID")
    UUID userId,
    @NotNull @NotBlank
    @Schema(description = "채널 ID")
    UUID channelId,
    @NotNull @NotBlank
    @Schema(description = "읽음 시간")
    Instant lastReadAt
) {

}
