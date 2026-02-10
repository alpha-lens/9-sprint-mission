package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UpdateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exepction.DoNotDuplicate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedFound;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> idUserMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> userNameIdMap = new ConcurrentHashMap<>();

    @Override
    public UUID create(CreateUserDto dto) {
        User user = dto.toEntity();
        userNameIdMap.put(user.getName(), user.getId());
        idUserMap.put(user.getId(), user);
        
        return user.getId();
    }

    @Override
    public boolean update(UpdateUserDto requestDto) {
        UUID userId = requestDto.id();
        String reName = requestDto.reName();
        String rePassword = requestDto.rePassword();
        String reMail = requestDto.reMail();
        String rePhoneNumber = requestDto.rePhoneNumber();
        UUID reProfileId = requestDto.reProfileId();

        idUserMap.get(userId).updateUser(reName, rePassword, reMail, rePhoneNumber, reProfileId);
        return true;
    }

    @Override
    public UserFinder find(String name) {
        User user = idUserMap.get(userNameIdMap.get(name));
        UUID id = user.getId();
        String userName = user.getName();

        return new UserFinder(id, userName, user.toString(), user.getProfileId());
    }

    @Override
    public UserFinder find(UUID userId) {
        User user = idUserMap.getOrDefault(userId, null);
        if(user == null) {
            throw new NotFound("Not Found This User Id");
        }
        String userName = user.getName();

        return new UserFinder(userId, userName, user.toString(), user.getProfileId());
    }

    @Override
    public List<UserFinder> findAll() {
        List<UserFinder> result = new ArrayList<>();
        idUserMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> {
            UUID id = user.getId();
            String userName = user.getName();
            result.add(new UserFinder(id, userName, user.toString(), user.getProfileId()));
        });
        return result;
    }

    @Override
    public boolean delete(UUID id) {
        String name = idUserMap.get(id).getName();
        try {
            userNameIdMap.remove(idUserMap.get(id).getName());
            idUserMap.remove(id);
        } catch (Exception e) {
            throw new FailedDelete("Delete is failed : " + name);
        }
        return true;
    }

    @Override
    public UUID userNameToId(String name) {
        try {
            return userNameIdMap.get(name);
        } catch (Exception e) {
            throw new FailedFound("Do not found this user : " + name);
        }
    }

    @Override
    public String userIdToName(UUID id) {
        try {
            return idUserMap.get(id).getName();
        } catch (Exception e) {
            throw new FailedFound("Do not found this user : " + id);
        }
    }

    @Override
    public boolean checkInvalid(UUID id, String pw) {
        try {
            return !idUserMap.get(id).getPassword().equals(pw);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void duplicateChecker(String checkThis, String findThis) {
        if(findThis == null || findThis.isEmpty()) return;

        switch (checkThis) {
            case "이메일":
                if(idUserMap.values().stream().anyMatch(u ->
                        findThis.equals(u.getEmail()))) {
                    throw new DoNotDuplicate("This email already exists: " + findThis);
                }
                break;
            case "전화번호":
                if (idUserMap.values().stream().anyMatch(u ->
                        findThis.equals(u.getPhoneNumber()))) {
                    throw new DoNotDuplicate("This phoneNumber already exists: " + findThis);
                }
                break;
            case "사용자명":
                if (userNameIdMap.containsKey(findThis)) {
                    throw new DoNotDuplicate("This userName already exists: " + findThis);
                }
                break;
        }
    }
}
