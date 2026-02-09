package com.sprint.mission.discodeit.exepction;

public class FailedLogin extends RuntimeException {
  public FailedLogin(String message) {
    super(message);
  }
}
