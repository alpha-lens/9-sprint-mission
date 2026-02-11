package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record RequestCreateUserDto(
        String username,
        String password,
        String email,
        UUID profileId
) {
    public User toEntity() {

        return new User(username, password, email, profileId);
    }
}
