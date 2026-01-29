package com.sprint.mission.discodeit;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UserState {
	private String username = "";

	public void userState(boolean isLogin, String username) {
		if(isLogin) {
			this.username = "";
		}
		this.username = username;
	}
}
