package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserStatusDto;
import com.sprint.mission.discodeit.dto.DeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.FindUserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusUpdateDto;

public interface UserStatusService {
    void create(CreateUserStatusDto requestDto);

    String find(FindUserStatusDto requestDto);

    void update(UserStatusUpdateDto requestDto);

    void delete(DeleteUserStatusDto requestDto);
}
