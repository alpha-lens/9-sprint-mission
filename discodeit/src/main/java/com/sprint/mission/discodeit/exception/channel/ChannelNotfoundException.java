package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelNotfoundException extends ChannelException {

  public ChannelNotfoundException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }
}
