package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final BasicBinaryContentService binaryContentService;
    private final UserService userService;
    private final UserState userState;
    private final UserStatusService userStatusService;

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

        UUID userId = userService.find(userName).id();

        userStatusService.create(new CreateUserStatusDto(userName, userId));

        return new ResponseEntity<>(userName + " has been created!\n"
                + "User ID: " + userId.toString()
                , HttpStatus.CREATED);
    }

    @RequestMapping(value = "/find/{userName}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> handleFindUserByName(@PathVariable String userName) {
        Map<String, String> result = new HashMap<>();
        UserFinder userFinder = userService.find(userName);
        String userStatus = userStatusService.find(new FindUserStatusDto(userFinder.id(), userName));

        result.put("userId", userFinder.id().toString());
        result.put("userProfile", userFinder.profileId().toString());
        result.put("userName", userName);
        result.put("userInfo", userFinder.userInfo());
        result.put("userStatus", userStatus);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, String>>> handleFindAllUser() {
        List<UserFinder> userFinders = userService.findAll();
        List<Map<String, String>> result = new ArrayList<>();

        for(UserFinder userFinder : userFinders) {
            Map<String, String> temp = new HashMap<>();
            FindUserStatusDto findUserStatusDto = new FindUserStatusDto(userFinder.id(), userFinder.name());

            UUID profileId = userFinder.profileId();

            temp.put("userId", userFinder.id().toString());
            if(profileId != null)
                temp.put("userProfile", profileId.toString());
            temp.put("userName", userFinder.name());
            temp.put("userInfo", userFinder.userInfo());
            temp.put("userStatus", userStatusService.find(findUserStatusDto));

            result.add(temp);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
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

    // ONLY TEST
    // 사용자 온라인 상태 정보 업데이트용.
    @RequestMapping(value = "/debug/userstatus")
    public ResponseEntity<String> handleUserStatus(
            @RequestParam("userName") String userName,
            @RequestParam(value = "minute") int minute
    ) {
        UUID userId = userService.userNameToId(userName);
        Instant now = Instant.now();
        Instant adjustedTime = now.minus(minute, ChronoUnit.MINUTES);

        UserStatusUpdateDto updateRequestDto = new UserStatusUpdateDto(userId, userName, adjustedTime);

        userStatusService.update(updateRequestDto);
        return new  ResponseEntity<>(userName + " status updated!\n"
                + "User Status : " +  userStatusService.find(new FindUserStatusDto(userId, userName))
                , HttpStatus.OK);
    }
}

/*
* 사용자 관리
* [x] 사용자를 등록할 수 있다.
* [x] 사용자 정보를 수정할 수 있다.
* [X] 사용자를 삭제할 수 있다.
* [X] 사용자를 조회할 수 있다.
* [X] 모든 사용자를 조회할 수 있다.
* [X] 사용자의 온라인 상태를 업데이트할 수 있다.
* */