package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
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
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final BasicBinaryContentService binaryContentService;
    private final UserService userService;
    private final UserStatusService userStatusService;

    @ResponseBody
    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ResponseUserDto handleCreateUser(
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
            binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(file.getContentType(), file.getName(), file.getBytes());
            UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
            userCreateRequestDto = new RequestCreateUserDto(userName, password, email, profileId);
        }

        ResponseUserDto responseDto = userService.create(userCreateRequestDto);

        UUID userId = responseDto.id();

        userStatusService.create(new RequestCreateUserStatusDto(userName, userId));

        return responseDto;
    }

    @ResponseBody
    @RequestMapping(value = "/find/{id}", method = RequestMethod.GET)
    public UserDto handleFindUserById(@PathVariable UUID id) {
        ResponseUserDto responseUserDto = userService.find(id);
        Boolean isOnline = userStatusService.find(new RequestFindUserStatusDto(responseUserDto.id(), responseUserDto.username()));
        return UserDto.from(responseUserDto, isOnline);
    }

    @ResponseBody
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public List<UserDto> handleFindAllUser() {
        List<ResponseUserDto> responseUserDtos = userService.findAll();
        List<UserDto> result = new ArrayList<>();

        for(ResponseUserDto responseUserDto : responseUserDtos) {
            Boolean isOnline = userStatusService.find(new RequestFindUserStatusDto(responseUserDto.id(), responseUserDto.username()));
            result.add(UserDto.from(responseUserDto, isOnline));
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
            updateUserRequestDto = new RequestUpdateUserDto(id, userName, password, email, phoneNumber, null);
        } else {
            binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(file.getContentType(), file.getOriginalFilename(), file.getBytes());
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
        String userName = userService.find(id).username();
        userService.delete(id);
        return new ResponseEntity<>("Success deleted! : " + userName, HttpStatus.OK);
    }

    @RequestMapping(value = "/userstatus/update", method = RequestMethod.PUT)
    public ResponseEntity<String> handleUserStatus(
            @RequestParam("id") UUID id
    ) {
        String userName = userService.find(id).username();
        Instant now = Instant.now();

        RequestUpdateUserStatusDto updateRequestDto = new RequestUpdateUserStatusDto(id, userName, now);

        userStatusService.update(updateRequestDto);
        return new  ResponseEntity<>(userName + " status updated!\n"
                + "User Status : " +  userStatusService.find(new RequestFindUserStatusDto(id, userName))
                , HttpStatus.OK);
    }
}