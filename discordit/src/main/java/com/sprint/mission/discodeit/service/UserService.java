package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Scanner;

public interface UserService {
//    void data();
    void createUser(Scanner sc);
    void updateUser(Scanner sc);
    void getUserName(User user);
    void getAllUserName();
    void deleteUser(Scanner sc);
}
