package com.sprint.mission.discodeit.exepction.global;

import com.sprint.mission.discodeit.dto.ErrorResponse;
import com.sprint.mission.discodeit.exepction.DoNotDuplicate;
import com.sprint.mission.discodeit.exepction.FailedCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoNotDuplicate.class)
    public ResponseEntity<ErrorResponse> handleDoNotDuplicate(DoNotDuplicate ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "이미 존재합니다: ",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(FailedCreate.class)
    public ResponseEntity<ErrorResponse> handleFailedCreate(FailedCreate ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "사용자 생성 실패",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(error);
    }
}