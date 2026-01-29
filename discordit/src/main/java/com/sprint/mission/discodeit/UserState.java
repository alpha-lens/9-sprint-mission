package com.sprint.mission.discodeit;

public class UserState {
	private String currentUser;

	private UserState() {}
	private static class Holder {
        private static final UserState INSTANT = new UserState();
	}
	public UserState getInstant() {
		return Holder.INSTANT;
	}

	public boolean userState(boolean isLogin, String username) {
		if(isLogin) currentUser = null;
		currentUser = username;
		return true;
	}
}
