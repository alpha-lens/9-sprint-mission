package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PublicChannelUpdateRequest(
    @NotNull @NotBlank
    String newName,
    String newDescription
) {

}
