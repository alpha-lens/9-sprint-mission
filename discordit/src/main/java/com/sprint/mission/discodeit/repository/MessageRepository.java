package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Message save(Message message);

  Optional<Message> findById(UUID id);

  //  @Query("select m from Message m where m.channel = :channel order by m.createdAt desc")
//  Optional<Message> findFirstByChannelOrderByCreatedAtDesc(Channel channel);

  @Query("select m.channel.id, max(m.createdAt) from Message m " +
      "where m.channel.id in :channelIds group by m.channel.id")
  List<Object[]> findLastMessageAtByChannelIds(@Param("channelIds") List<UUID> channelIds);

  List<Message> findAllByChannelId(UUID channelId);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannelId(UUID channelId);

  Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

  Slice<Message> findAllSliceByChannelId(UUID channelId, Pageable pageable);

}
