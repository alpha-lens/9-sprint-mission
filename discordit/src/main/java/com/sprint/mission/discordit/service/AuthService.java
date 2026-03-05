package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.request.LoginRequest;
import com.sprint.mission.discordit.entity.User;

public interface AuthService {

  User login(LoginRequest loginRequest);
}
