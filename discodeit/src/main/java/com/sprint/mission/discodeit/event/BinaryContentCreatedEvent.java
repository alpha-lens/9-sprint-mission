package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContentCreatedEvent {
  private final UUID binaryContentId;
  private final byte[] fileData;
  private final String fileName;
}