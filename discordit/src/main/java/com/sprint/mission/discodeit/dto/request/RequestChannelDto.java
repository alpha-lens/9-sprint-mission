package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChannelDto {

  private UUID id;
  private String name;
  private String description;
  private UUID createUserId;
  private List<UUID> participantIds;
}
