package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final BasicBinaryContentService binaryContentService;
    private final UserService userService;

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ResponseEntity<String> handleCreateUser(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        CreateBinaryContentDto binaryContentCreateRequestDto;
        CreateUserDto userCreateRequestDto;

        if(file.isEmpty()) {
            userCreateRequestDto = new CreateUserDto(userName, password, email, null);
        } else {
            binaryContentCreateRequestDto = new CreateBinaryContentDto(AttachmentType.USER, file.getName(), file.getBytes());
            UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
            userCreateRequestDto = new CreateUserDto(userName, password, email, profileId);
        }

        userService.create(userCreateRequestDto);

        return new ResponseEntity<>(userName + "has been created!", HttpStatus.CREATED);
    }
}

/*
* 사용자 관리
* [ ] 사용자를 등록할 수 있다.
* [ ] 사용자 정보를 수정할 수 있다.
* [ ] 사용자를 삭제할 수 있다.
* [ ] 모든 사용자를 조회할 수 있다.
* [ ] 사용자의 온라인 상태를 업데이트할 수 있다.
* */