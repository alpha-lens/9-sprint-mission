package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    RequestUserResponseDto create(RequestCreateUserDto dto);

    RequestUserResponseDto update(RequestUpdateUserDto requestDto);

    RequestUserResponseDto find(String name);

    RequestUserResponseDto find(UUID userId);

    List<RequestUserResponseDto> findAll();

    boolean delete(UUID id);

    boolean isPresent(UUID id);

    UUID userNameToId(String name);

    String userIdToName(UUID id);

    boolean checkInvalid(UUID id, String pw);

    void duplicateChecker(String checkThis, String findThis);
}
