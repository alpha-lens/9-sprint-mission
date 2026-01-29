package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> usersMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> usersName = new ConcurrentHashMap<>();

    private JCFUserRepository() {}
    private static class Holder {
        private static final JCFUserRepository INSTANCE = new JCFUserRepository();
    }
    public static JCFUserRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean createUser(CreateUserDto dto) {
        List<Object> userSet = dto.toEntity();
        User user = (User) userSet.get(0);
        UserStatus userStatus = (UserStatus) userSet.get(1);

        if(isDuplicateName(user.getName())) return false;
        usersName.put(user.getName(), user.getId());
        usersMap.put(user.getId(), user);
        return true;
    }

    @Override
    public boolean updateUser(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber) {
        usersMap.get(userId).updateUser(reName, rePassword, reMail, rePhoneNumber);
        return false;
    }

    @Override
    public String getUser(String name) {
        return usersMap.get(usersName.get(name)).toString();
    }

    @Override
    public List<String> getAllUser() {
        List<String> result = new ArrayList<>();
        usersMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> result.add(user.toString()));
        return result;
    }

    @Override
    public boolean deleteUser(UUID id) {
        usersName.remove(usersMap.get(id).getName());
        usersMap.remove(id);
        return true;
    }

    /// check method
    private boolean isDuplicateName(String name) {
        try {
            usersName.get(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPresentUser(Object arg) {
        try {
            if(arg instanceof String) {
                usersMap.get(usersName.get((String) arg));
            } else if(arg instanceof UUID){
                usersMap.get((UUID) arg);
            } else {
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    public UUID userNameToId(String name) {
        try {
            return usersName.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    public String userIdToName(UUID id) {
        try {
            return usersMap.get(id).getName();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean check(UUID id, String pw) {
        try {
            return !usersMap.get(id).getPassword().equals(pw);
        } catch (Exception e) {
            return true;
        }
    }
}
