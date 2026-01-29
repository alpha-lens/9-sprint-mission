package com.sprint.mission.discodeit;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UserState {
	private String username = "";

	public void userState(String username) {
		this.username = username;
	}
}
