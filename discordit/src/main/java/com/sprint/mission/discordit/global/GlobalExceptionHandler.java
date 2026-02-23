package com.sprint.mission.discordit.global;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResult<String>> handleException(IllegalArgumentException e) {
    ApiError error = new ApiError("BAD_REQUEST", e.getMessage(), null);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResult.error(error));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiResult<String>> handleException(NoSuchElementException e) {
    ApiError error = new ApiError("NOT_FOUND", e.getMessage(), null);
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResult.error(error));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResult<String>> handleException(Exception e) {
    ApiError error = new ApiError("INTERNAL_SERVER_ERROR", e.getMessage(), null);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResult.error(error));
  }
}
