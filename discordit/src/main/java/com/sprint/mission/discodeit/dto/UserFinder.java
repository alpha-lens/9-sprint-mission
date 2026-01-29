package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public record UserFinder() {
    public static String from(User user, UserStatus userStatus) {
        return user.toString() + userStatus.toString() + "\n====================";
    }
}
