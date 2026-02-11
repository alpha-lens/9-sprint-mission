package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UUID userNameToId(String name);

    boolean isPresent(UUID id);

    boolean isInvalid(UUID userId, String password);

    UserResponseDto create(CreateUserDto requestDto);
    UserResponseDto update(UpdateUserDto requestDto);
    UserResponseDto find(String name);

    UserResponseDto find(UUID id);

    List<UserResponseDto> findAll();
    boolean delete(UUID id);
}