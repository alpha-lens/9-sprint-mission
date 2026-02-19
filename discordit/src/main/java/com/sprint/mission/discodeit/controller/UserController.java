package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestCreateUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestFindUserStatusDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

  private final BasicBinaryContentService binaryContentService;
  private final UserService userService;
  private final UserStatusService userStatusService;

  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public ResponseUserDto handleCreateUser(
      @RequestParam("username") String username,
      @RequestParam("password") String password,
      @RequestParam("email") String email,
      @RequestParam(value = "file", required = false) MultipartFile file
  ) throws IOException {
    RequestCreateBinaryContentDto binaryContentCreateRequestDto;
    RequestCreateUserDto userCreateRequestDto;

    if (file == null || file.isEmpty()) {
      userCreateRequestDto = new RequestCreateUserDto(username, password, email, null);
    } else {
      binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(file.getContentType(),
          file.getName(), file.getBytes());
      UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
      userCreateRequestDto = new RequestCreateUserDto(username, password, email, profileId);
    }

    ResponseUserDto responseDto = userService.create(userCreateRequestDto);

    UUID userId = responseDto.id();

    userStatusService.create(new RequestCreateUserStatusDto(username, userId));

    return responseDto;
  }

  @ResponseBody
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public UserDto handleFindUserById(@PathVariable UUID id) {
    ResponseUserDto responseUserDto = userService.find(id);
    Boolean isOnline = userStatusService.find(
        new RequestFindUserStatusDto(responseUserDto.id(), responseUserDto.username()));
    return UserDto.from(responseUserDto, isOnline);
  }

  @ResponseBody
  @RequestMapping(method = RequestMethod.GET)
  public List<UserDto> handleFindAllUser() {
    List<ResponseUserDto> responseUserDtos = userService.findAll();
    List<UserDto> result = new ArrayList<>();

    for (ResponseUserDto responseUserDto : responseUserDtos) {
      Boolean isOnline = userStatusService.find(
          new RequestFindUserStatusDto(responseUserDto.id(), responseUserDto.username()));
      result.add(UserDto.from(responseUserDto, isOnline));
    }

    return result;
  }

  @RequestMapping(value = "{id}", method = RequestMethod.PUT)
  public ResponseEntity<String> handleUpdateUser(
      @PathVariable UUID id,
      @RequestParam(value = "oldPassword") String oldPassword,
      @RequestParam(value = "username", required = false) String username,
      @RequestParam(value = "password", required = false) String password,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
      @RequestParam(value = "file", required = false) MultipartFile file
  ) throws IOException {
    RequestUpdateUserDto updateUserRequestDto;
    RequestCreateBinaryContentDto binaryContentCreateRequestDto;

    if (userService.isInvalid(id, oldPassword)) {
      throw new InvalidParameterException("Invalid user messageId or password");
    }

    if (file == null || file.isEmpty()) {
      updateUserRequestDto = new RequestUpdateUserDto(id, username, password, email, phoneNumber,
          null);
    } else {
      binaryContentCreateRequestDto = new RequestCreateBinaryContentDto(file.getContentType(),
          file.getOriginalFilename(), file.getBytes());
      UUID profileId = binaryContentService.create(binaryContentCreateRequestDto);
      updateUserRequestDto = new RequestUpdateUserDto(id, username, password, email, phoneNumber,
          profileId);
    }

    userService.update(updateUserRequestDto);
    return new ResponseEntity<>(username + " has been updated!", HttpStatus.OK);
  }

  @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
  public ResponseEntity<String> handleDeleteUser(
      @PathVariable UUID id
  ) {
    String username = userService.find(id).username();
    userService.delete(id);
    return new ResponseEntity<>("Success deleted! : " + username, HttpStatus.OK);
  }

  @RequestMapping(value = "{id}/userStatus", method = RequestMethod.PATCH)
  public ResponseEntity<String> handleUserStatus(
      @PathVariable UUID id
  ) {
    String username = userService.find(id).username();
    Instant now = Instant.now();

    RequestUpdateUserStatusDto updateRequestDto = new RequestUpdateUserStatusDto(id, username, now);

    userStatusService.update(updateRequestDto);
    return new ResponseEntity<>(username + " status updated!\n"
        + "User Status : " + userStatusService.find(new RequestFindUserStatusDto(id, username))
        , HttpStatus.OK);
  }
}