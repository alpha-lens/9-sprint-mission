package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentUploadFailureEvent(
    UUID receiverId, String errorMessage
) {}
