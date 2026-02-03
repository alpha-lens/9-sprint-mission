package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public record CreateUserDto(
        String username,
        String password,
        String profileImage
) {
    public List<Object> toEntity() {
        User user = new User(username, password);
        BinaryContent binaryContent;

        if(!profileImage.isEmpty()) {
            binaryContent = binaryContent(user.getId(), profileImage);
            user.setProfileId(binaryContent.getId());
        }

        return List.of(user,new UserStatus(user.getId()));
    }

    private BinaryContent binaryContent(UUID id, String profileImage) {
        return new BinaryContent(AttachmentType.USER, id, profileImage);
    }
}
