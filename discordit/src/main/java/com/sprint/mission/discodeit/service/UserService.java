package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;

import java.util.List;
import java.util.UUID;

public interface UserService {
    boolean isPresent(String name);

    boolean isValid(UUID userId, String password);

    boolean create(CreateUserDto requestDto);
    boolean update(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber, String reProfile);
    UserFinder find(String name);
    List<UserFinder> findAll();
    boolean delete(UUID id);
}