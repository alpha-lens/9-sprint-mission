package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    UserResponseDto create(CreateUserDto dto);

    UserResponseDto update(UpdateUserDto requestDto);

    UserResponseDto find(String name);

    UserResponseDto find(UUID userId);

    List<UserResponseDto> findAll();

    boolean delete(UUID id);

    boolean isPresent(UUID id);

    UUID userNameToId(String name);

    String userIdToName(UUID id);

    boolean checkInvalid(UUID id, String pw);

    void duplicateChecker(String checkThis, String findThis);
}
