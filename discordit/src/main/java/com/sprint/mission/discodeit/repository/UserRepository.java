package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    boolean createUser(CreateUserDto dto);

    boolean updateUser(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber);

    UserFinder find(String name);

    List<UserFinder> findAll();

    boolean deleteUser(UUID id);
}
