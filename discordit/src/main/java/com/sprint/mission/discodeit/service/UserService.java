package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UUID userNameToId(String name);

    boolean isPresent(UUID id);

    boolean isInvalid(UUID userId, String password);

    RequestUserResponseDto create(RequestCreateUserDto requestDto);
    RequestUserResponseDto update(RequestUpdateUserDto requestDto);
    RequestUserResponseDto find(String name);

    RequestUserResponseDto find(UUID id);

    List<RequestUserResponseDto> findAll();
    boolean delete(UUID id);
}