package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestDeleteUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;

import java.util.List;

public interface UserStatusRepository {
    boolean create(RequestCreateUserStatusDto requestDto);

    String find(RequestFindUserStatusDto requestDto);

    List<String> findAll(List<RequestFindUserStatusDto> requestDto);

    boolean update(RequestUpdateUserStatusDto requestDto);

    boolean delete(RequestDeleteUserStatusDto requestDto);
}
