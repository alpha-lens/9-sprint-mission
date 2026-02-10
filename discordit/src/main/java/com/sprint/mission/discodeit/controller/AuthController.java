package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> loginUser(
            @RequestParam(value = "userName") String userName,
            @RequestParam(value = "password") String password
    ) {
        authService.login(new LoginDto(userName, password));
        return ResponseEntity.ok(userName + " is login successfully");
    }
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> loginUser() {
        authService.logout();
        return ResponseEntity.ok("Logout successfully");
    }
}

/*
* 권한 관리
* [X] 사용자는 로그인할 수 있다.
* */