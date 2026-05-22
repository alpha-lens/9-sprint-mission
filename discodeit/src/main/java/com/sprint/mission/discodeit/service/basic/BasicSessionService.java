package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.service.SessionService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicSessionService implements SessionService {

  private final JwtRegistry jwtRegistry;

  @Override
  public Set<String> getOnlineUsernames() {
    return jwtRegistry.getAllOnlineUsernames();
  }

  @Override
  public boolean isUserOnline(String username) {
    return jwtRegistry.hasActiveJwtInformationByUsername(username);
  }
}
