package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record RequestUpdateUserDto(
        UUID id, String reName, String rePassword, String reMail, String rePhoneNumber, UUID reProfileId
) {
}
