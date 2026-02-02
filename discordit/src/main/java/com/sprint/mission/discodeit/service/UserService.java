package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    boolean isPresent(String name);

    boolean isValid(String password);

    boolean create(CreateUserDto requestDto);
    boolean update(String reName, String rePassword, String reMail, String rePhoneNumber, String reProfile);
    String find(String name);
    List<String> findAll();
    boolean delete(UUID id);
}