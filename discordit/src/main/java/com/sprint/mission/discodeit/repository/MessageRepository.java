package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  @Query("select m.channel.id, max(m.createdAt) from Message m " +
      "where m.channel.id in :channelIds group by m.channel.id")
  List<Object[]> findLastMessageAtByChannelIds(@Param("channelIds") List<UUID> channelIds);

  @EntityGraph(attributePaths = {"attachments", "author"})
  List<Message> findAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);

  @EntityGraph(attributePaths = {"attachments", "author"})
  @Query("SELECT m FROM Message m WHERE m.channel.id = :channelId "
      + "AND (CAST(:cursor AS timestamp) IS NULL OR m.createdAt < :cursor)")
  Slice<Message> findAllByChannelId(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor, Pageable pageable);
}
