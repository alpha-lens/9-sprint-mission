package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Scanner;

public interface UserService {
    void data();
    void createUser(Scanner sc, List<User> users);
    void updateUser(Scanner sc, List<User> users);
    void getUserName(User user);
    void getAllUserName(List<User> users);
    void deleteUser(Scanner sc, List<User> users);
}
