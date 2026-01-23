package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileUserRepository implements UserRepository {
    private final Map<UUID, User> usersMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> usersName = new ConcurrentHashMap<>();
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
                        usersMap.put(user.getId(), user);
                        usersName.put(user.getName(), user.getId());
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
    public boolean createUser(String name, String pw) {
        User user = new User(name, pw);
        usersName.put(name, user.getId());
        usersMap.put(user.getId(), user);
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
            oos.writeObject(userId);
            usersMap.get(userId).updateUser(reName, rePassword, reMail, rePhoneNumber);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getUser(String name) {
        return usersMap.get(usersName.get(name)).toString();
    }

//    @Override
//    public void getUserName() {
//
//    }

    @Override
    public List<String> getAllUser() {
        List<String> result = new ArrayList<>();
        usersMap.values().stream().sorted(Comparator.comparing(User::getName)).forEach(user -> result.add(user.toString()));
        return result;
    }

    @Override
    public void deleteUser(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        usersName.remove(usersMap.get(id).getName());
        usersMap.remove(id);
    }

    public UUID check(String name) {
        try {
            return usersName.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean check(UUID id) {
        try {
            usersMap.get(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean check(UUID id, String pw) {
        return usersMap.get(id).getPw().equals(pw);
    }
}
