package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  ReadStatus save(ReadStatus readStatus);

  Optional<ReadStatus> findById(UUID id);

  List<ReadStatus> findAllByUser_Id(UUID userId);

  List<ReadStatus> findAllByChannel_Id(UUID channelId);

  @Query("select rs from ReadStatus rs " +
      "join fetch rs.user u " +
      "left join fetch u.status " +
      "where rs.channel.id in :channelIds")
  List<ReadStatus> findAllByChannelIdsWithUser(List<UUID> channelIds);

  boolean existsById(UUID id);

  void deleteById(UUID id);

  void deleteAllByChannel_Id(UUID channelId);
}
