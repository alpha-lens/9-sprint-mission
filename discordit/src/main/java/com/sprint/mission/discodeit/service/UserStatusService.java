package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestDeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;

public interface UserStatusService {
    void create(RequestCreateUserStatusDto requestDto);

    String find(RequestFindUserStatusDto requestDto);

    void update(RequestUpdateUserStatusDto requestDto);

    void delete(RequestDeleteUserStatusDto requestDto);
}
