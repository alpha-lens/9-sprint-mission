package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    boolean createUser(String name, String pw);

    boolean updateUser(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber);

    String getUser(String name);

    List<String> getAllUser();

    boolean deleteUser(UUID id);
}
