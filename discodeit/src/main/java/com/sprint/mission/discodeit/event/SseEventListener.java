package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEventListener {

  private final SseService sseService;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleSseEvent(SseEvent event) {
    log.info("SSE Event Listener received event: {}, receiverIds size: {}",
        event.eventName(), event.receiverIds() != null ? event.receiverIds().size() : "broadcast");

    if (event.receiverIds() == null) {
      sseService.broadcast(event.eventName(), event.data());
    } else {
      sseService.send(event.receiverIds(), event.eventName(), event.data());
    }
  }
}
