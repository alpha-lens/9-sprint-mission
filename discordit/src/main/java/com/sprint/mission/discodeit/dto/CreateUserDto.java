package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;

public record CreateUserDto(
        String username,
        String password,
        String profileImage
) {
    public List<Object> toEntity() {
        BinaryContent binaryContent = binaryContent(profileImage);
        if(binaryContent == null) {
            User user = new User(username, password);
            return List.of(user, new UserStatus(user.getId()));
        }

        User user = new User (username, password, binaryContent.getId());
        return List.of(user,new UserStatus(user.getId()));
    }

    private BinaryContent binaryContent(String profileImage) {
        if(profileImage.trim().isEmpty()) return null;
        return new BinaryContent(profileImage);
    }
}
