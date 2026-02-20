package com.sprint.mission.discordit.service;

import com.sprint.mission.discordit.dto.data.LoginInfoDto;
import com.sprint.mission.discordit.dto.request.LoginRequest;

public interface AuthService {

  LoginInfoDto login(LoginRequest loginRequest);
}
