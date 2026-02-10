package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final BasicBinaryContentService binaryContentService;
    private final UserService userService;
    private final UserState userState;

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ResponseEntity<String> handleCreateUser(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        CreateBinaryContentDto binaryContentCreateRequestDto;
        CreateUserDto userCreateRequestDto;

        if(file == null || file.isEmpty()) {
            userCreateRequestDto = new CreateUserDto(userName, password, email, null);
        } else {
            binaryContentCreateRequestDto = new CreateBinaryContentDto(AttachmentType.USER, file.getName(), file.getBytes());
            UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
            userCreateRequestDto = new CreateUserDto(userName, password, email, profileId);
        }

        userService.create(userCreateRequestDto);

        return new ResponseEntity<>(userName + "has been created!\n"
                + "User ID: " + userService.find(userName).id()
                , HttpStatus.CREATED);
    }

    @RequestMapping(value = "/find/{userName}", method = RequestMethod.GET)
    public ResponseEntity<UserFinder> handleFindUserByName(@PathVariable String userName) {
        UserFinder userFinder = userService.find(userName);
        return new ResponseEntity<>(userFinder, HttpStatus.OK);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserFinder>> handleFindAllUser() {
        List<UserFinder> userFinders = userService.findAll();
        return new ResponseEntity<>(userFinders, HttpStatus.OK);
    }

    @RequestMapping(value="/update", method= RequestMethod.PUT)
    public ResponseEntity<String> handleUpdateUser(
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        UpdateUserDto updateUserRequestDto;
        CreateBinaryContentDto binaryContentCreateRequestDto;

        if(file == null || file.isEmpty()) {
            updateUserRequestDto = new UpdateUserDto(userState.getUserId(), userName, password, email, phoneNumber, null);;
        } else {
            binaryContentCreateRequestDto = new CreateBinaryContentDto(AttachmentType.USER, file.getName(), file.getBytes());
            UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
            updateUserRequestDto = new UpdateUserDto(userState.getUserId(), userName, password, email, phoneNumber, profileId);
        }

        userService.update(updateUserRequestDto);
        return new ResponseEntity<>(userState.getUserName() + " has been updated!", HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> handleDeleteUser(
            @RequestParam("userName") String userName
    ) {
        userService.delete(userService.userNameToId(userName));
        return new ResponseEntity<>("Success deleted! : " + userName, HttpStatus.OK);
    }
}

/*
* 사용자 관리
* [x] 사용자를 등록할 수 있다.
* [x] 사용자 정보를 수정할 수 있다.
* [X] 사용자를 삭제할 수 있다.
* [X] 사용자를 조회할 수 있다.
* [X] 모든 사용자를 조회할 수 있다.
* [ ] 사용자의 온라인 상태를 업데이트할 수 있다.
* */