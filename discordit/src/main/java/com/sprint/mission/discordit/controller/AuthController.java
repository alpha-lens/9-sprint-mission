package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.data.LoginInfoDto;
import com.sprint.mission.discordit.dto.request.LoginRequest;
import com.sprint.mission.discordit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  @RequestMapping(path = "login", method = RequestMethod.POST)
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    LoginInfoDto login = authService.login(loginRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(login.username() + " is success login");
  }
}
