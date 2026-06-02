package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.service.NotificationService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NotificationService notificationService;

  private DiscodeitUserDetails userDetails;
  private UUID userId;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    UserDto userDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        Role.USER,
        true,
        null
    );
    userDetails = new DiscodeitUserDetails(userDto, "password");
  }

  @Test
  @DisplayName("알림 목록 조회 성공 테스트")
  void findNotification_Success() throws Exception {
    // Given
    UUID notificationId = UUID.randomUUID();
    Instant now = Instant.now();
    NotificationDto notificationDto = new NotificationDto(
        notificationId,
        now,
        userId,
        "test sender (#general)",
        "hello world"
    );

    given(notificationService.findAllByReceiverId(userId))
        .willReturn(List.of(notificationDto));

    // When & Then
    mockMvc.perform(get("/api/notifications")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(notificationId.toString()))
        .andExpect(jsonPath("$[0].receiverId").value(userId.toString()))
        .andExpect(jsonPath("$[0].title").value("test sender (#general)"))
        .andExpect(jsonPath("$[0].content").value("hello world"));
  }

  @Test
  @DisplayName("알림 확인(삭제) 성공 테스트")
  void deleteNotification_Success() throws Exception {
    // Given
    UUID notificationId = UUID.randomUUID();

    // When & Then
    mockMvc.perform(delete("/api/notifications/{notificationId}", notificationId)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("알림 확인(삭제) 실패 테스트 - 존재하지 않는 알림")
  void deleteNotification_Failure_NotFound() throws Exception {
    // Given
    UUID nonExistentId = UUID.randomUUID();
    willThrow(NotificationNotFoundException.withId(nonExistentId))
        .given(notificationService).delete(nonExistentId, userId);

    // When & Then
    mockMvc.perform(delete("/api/notifications/{notificationId}", nonExistentId)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("알림 확인(삭제) 실패 테스트 - 타인의 알림")
  void deleteNotification_Failure_Forbidden() throws Exception {
    // Given
    UUID otherNotificationId = UUID.randomUUID();
    willThrow(new AccessDeniedException("요청자 본인의 알림에 대해서만 수행할 수 있습니다."))
        .given(notificationService).delete(otherNotificationId, userId);

    // When & Then
    mockMvc.perform(delete("/api/notifications/{notificationId}", otherNotificationId)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
