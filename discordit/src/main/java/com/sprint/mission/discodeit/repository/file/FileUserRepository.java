package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.CreateUserDto;
import com.sprint.mission.discodeit.dto.UserFinder;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FileUserRepository implements UserRepository {
    private final Map<UUID, User> idUserMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> userNameIdMap = new ConcurrentHashMap<>();
    private final Map<UUID, UserStatus> userStatusMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private FileUserRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /// 파일에서 user값을 불러와, hashMap으로 다시 저장하는 과정
        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(user -> {
                        idUserMap.put(user.getId(), user);
                        userNameIdMap.put(user.getName(), user.getId());
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private static final FileUserRepository INSTANCE = new FileUserRepository();
    }

    public static FileUserRepository getInstance() {
        return Holder.INSTANCE;
    }

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public boolean createUser(CreateUserDto dto) {
        List<Object> userSet = dto.toEntity();
        User user = (User) userSet.get(0);
        UserStatus userStatus = (UserStatus) userSet.get(1);
        userNameIdMap.put(user.getName(), user.getId());
        idUserMap.put(user.getId(), user);
        userStatusMap.put(user.getId(), userStatus);
        Path path = resolvePath(user.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean updateUser(UUID userId, String reName, String rePassword, String reMail, String rePhoneNumber) {
        Path path = resolvePath(userId);

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(idUserMap.get(userId));
            idUserMap.get(userId).updateUser(reName, rePassword, reMail, rePhoneNumber);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getUser(String name) {
        User user = idUserMap.get(userNameIdMap.get(name));
        return UserFinder.from(user, userStatusMap.get(user.getId()));
    }

    @Override
    public List<String> getAllUser() {
        List<String> result = new ArrayList<>();
        idUserMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> result.add(UserFinder.from(user, userStatusMap.get(user.getId()))));
        return result;
    }

    @Override
    public boolean deleteUser(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            return false;
        }
        userStatusMap.remove(id);
        userNameIdMap.remove(idUserMap.get(id).getName());
        idUserMap.remove(id);
        return true;
    }

    public UUID userNameToId(String name) {
        try {
            return userNameIdMap.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    public String userIdToName(UUID id) {
        try {
            return idUserMap.get(id).getName();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean check(UUID id, String pw) {
        try {
            return !idUserMap.get(id).getPassword().equals(pw);
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isPresentThis(String checkThis, String findThis) {
        try {
            if (checkThis.equals("이메일") && idUserMap.values().stream().anyMatch(u -> u.getEmail().equals(findThis))) return true;
            if (checkThis.equals("전화번호") && idUserMap.values().stream().anyMatch(u -> u.getPhoneNumber().equals(findThis))) return true;
            if (checkThis.equals("사용자명") && userNameIdMap.get(findThis) != null) return true;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean isPresentUser(Object arg) {
        try {
            if(arg instanceof String) {
                idUserMap.get(userNameIdMap.get((String) arg));
            } else if(arg instanceof UUID){
                idUserMap.get((UUID) arg);
            } else {
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }
}
