package com.sprint.mission.discodeit.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse<T> {

  private List<T> content;
  private Object nextCursor;
  private int size;
  private boolean hasNext;
  private Long totalElements;
}
