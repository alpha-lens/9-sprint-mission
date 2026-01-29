package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public record UserFinder() {
    public static String from(User user, UserStatus userStatus) {
        if(userStatus == null ) {
            return user.toString() + "\n오프라인\n====================";
        }
        return user.toString() + "\n" + userStatus.toString() + "\n====================";
    }
}
