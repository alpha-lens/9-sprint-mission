package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateUserDto;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    boolean createUser(CreateUserDto dto);

    boolean updateUser(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber);

    String getUser(String name);

    List<String> getAllUser();

    boolean deleteUser(UUID id);
}
