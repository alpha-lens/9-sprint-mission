package com.sprint.mission.discordit.dto.request;

public record UserCreateRequest(
    String username,
    String email,
    String password
) {

}
