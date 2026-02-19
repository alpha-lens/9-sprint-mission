package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestLoginDto;
import com.sprint.mission.discodeit.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<String> loginUser(
      @RequestParam(value = "username") String username,
      @RequestParam(value = "password") String password
  ) {
    authService.login(new RequestLoginDto(username, password));
    return ResponseEntity.ok(username + " is login successfully");
  }

  @RequestMapping(value = "/logout", method = RequestMethod.POST)
  public ResponseEntity<String> loginUser() {
    authService.logout();
    return ResponseEntity.ok("Logout successfully");
  }
}