package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final BasicBinaryContentService binaryContentService;
    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public RequestUserResponseDto handleCreateUser(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        RequestCreateBinaryContentDto binaryContentCreateRequestDto;
        RequestCreateUserDto userCreateRequestDto;

        if(file == null || file.isEmpty()) {
            userCreateRequestDto = new RequestCreateUserDto(userName, password, email, null);
        } else {
            binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(BinaryContentType.USER, file.getName(), file.getBytes());
            UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
            userCreateRequestDto = new RequestCreateUserDto(userName, password, email, profileId);
        }

        RequestUserResponseDto responseDto = userService.create(userCreateRequestDto);

        UUID userId = responseDto.id();

        userStatusService.create(new RequestCreateUserStatusDto(userName, userId));

        return responseDto;
    }

    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> handleFindUserByName(@PathVariable UUID id) {
        Map<String, String> result = new HashMap<>();
        RequestUserResponseDto requestUserResponseDto = userService.find(id);
        String userStatus = userStatusService.find(new RequestFindUserStatusDto(requestUserResponseDto.id(), requestUserResponseDto.name()));
        UUID profileId = requestUserResponseDto.profileId();

        result.put("userId", requestUserResponseDto.id().toString());
        if(profileId != null)
            result.put("userProfile", profileId.toString());;
        result.put("userName", requestUserResponseDto.name());
        result.put("userInfo", requestUserResponseDto.userInfo());
        result.put("userStatus", userStatus);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<Map<String, String>> handleFindAllUser() {
        List<RequestUserResponseDto> requestUserResponseDtos = userService.findAll();
        List<Map<String, String>> result = new ArrayList<>();

        for(RequestUserResponseDto requestUserResponseDto : requestUserResponseDtos) {
            Map<String, String> temp = new HashMap<>();
            RequestFindUserStatusDto requestFindUserStatusDto = new RequestFindUserStatusDto(requestUserResponseDto.id(), requestUserResponseDto.name());

            UUID profileId = requestUserResponseDto.profileId();

            temp.put("userId", requestUserResponseDto.id().toString());
            if(profileId != null)
                temp.put("userProfile", profileId.toString());
            temp.put("userName", requestUserResponseDto.name());
            temp.put("userInfo", requestUserResponseDto.userInfo());
            temp.put("userStatus", userStatusService.find(requestFindUserStatusDto));

            result.add(temp);
        }

        return result;
    }

    @RequestMapping(value="/update", method= RequestMethod.PUT)
    public ResponseEntity<String> handleUpdateUser(
            @RequestParam(value = "id") UUID id,
            @RequestParam(value = "oldPassword")  String oldPassword,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws IOException {
        RequestUpdateUserDto updateUserRequestDto;
        RequestCreateBinaryContentDto binaryContentCreateRequestDto;

        if(userService.isInvalid(id, oldPassword))
            throw new InvalidParameterException("Invalid user messageId or password");

        if(file == null || file.isEmpty()) {
            updateUserRequestDto = new RequestUpdateUserDto(id, userName, password, email, phoneNumber, null);;
        } else {
            binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(BinaryContentType.USER, file.getName(), file.getBytes());
            UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
            updateUserRequestDto = new RequestUpdateUserDto(id, userName, password, email, phoneNumber, profileId);
        }

        userService.update(updateUserRequestDto);
        return new ResponseEntity<>(userName + " has been updated!", HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> handleDeleteUser(
            @RequestParam("id") UUID id
    ) {
        String userName = userService.find(id).name();
        userService.delete(id);
        return new ResponseEntity<>("Success deleted! : " + userName, HttpStatus.OK);
    }

    @RequestMapping(value = "/userstatus/update")
    public ResponseEntity<String> handleUserStatus(
            @RequestParam("id") UUID id
    ) {
        String userName = userService.find(id).name();
        Instant now = Instant.now();

        RequestUpdateUserStatusDto updateRequestDto = new RequestUpdateUserStatusDto(id, userName, now);

        userStatusService.update(updateRequestDto);
        return new  ResponseEntity<>(userName + " status updated!\n"
                + "User Status : " +  userStatusService.find(new RequestFindUserStatusDto(id, userName))
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