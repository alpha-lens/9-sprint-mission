package com.sprint.mission.discodeit.service.auth;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.exepction.FailedLogin;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserState userState;

    public void login(LoginDto requestDto) {
        /// 검증로직
        try {
            UUID id = userRepository.userNameToId(requestDto.name());

            if(!userRepository.checkInvalid(id, requestDto.password())) {
                userState.userState(requestDto.name(), id);
                userStatusRepository.update(new UserStatusUpdateDto(id, requestDto.name(), null));
            } else throw new Exception();
        } catch (Exception ignore) {
            throw new FailedLogin("Invalid username or password");
        }
    }

    public void logout() {
        userStatusRepository.update(new UserStatusUpdateDto(userState.getUserId(), userState.getUserName(), null));
        userState.userState("");
    }
}
