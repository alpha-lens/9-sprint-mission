package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsLogin {
    private final UserState userState;
    public boolean check(String service, int menu) {
        boolean isLogin = !userState.getUserName().isEmpty();
        if(service.equals("User")) {
            return isLogin || menu != 4;
        }
        return isLogin || menu == 3;
    }
}
