package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(MessageCreatedEvent event) {
    UUID authorId = event.message().getAuthor().getId();
    UUID channelId = event.message().getChannel().getId();
    String title = event.message().getAuthor().getUsername() + " (#" +
        event.message().getChannel().getName() + ")";
    String content = event.message().getContent();

    readStatusRepository.findAllByChannelIdWithUser(channelId)
        .stream()
        .filter(rs -> !rs.getUser().getId().equals(authorId) && rs.isNotificationEnabled())
        .forEach(rs -> notificationRepository.save(
            new Notification(rs.getUser().getId(), title, content)
        ));
  }

  @TransactionalEventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void on(RoleUpdatedEvent event) {
    UUID userId = event.userId();
    String title = "권한이 변경되었습니다.";
    String content = event.oldRole() + " -> " + event.newRole();

    notificationRepository.save(new Notification(userId, title, content));
  }
}
