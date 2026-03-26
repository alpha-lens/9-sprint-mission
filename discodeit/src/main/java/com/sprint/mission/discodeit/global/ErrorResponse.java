package com.sprint.mission.discodeit.global;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp, String code, String message, Map<String, Object> details,
    String exceptionType, int staus
) {

  public static ErrorResponse from(DiscodeitException e) {
    ErrorCode code = e.getErrorCode();
    return new ErrorResponse(
        e.getTimestamp(),
        code.name(),
        code.getMessage(),
        e.getDetails(),
        e.getClass().getSimpleName(),
        code.getStatus()
    );
  }
}
