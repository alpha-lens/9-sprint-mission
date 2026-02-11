package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.request.RequestCreateUserDto;
import com.sprint.mission.discodeit.dto.request.RequestUpdateUserDto;
import com.sprint.mission.discodeit.dto.response.ResponseUserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exepction.*;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("file")
public class FileUserRepository implements UserRepository {
    private final Map<UUID, User> idUserMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> userNameIdMap = new ConcurrentHashMap<>();
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

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
            throw new FailedInit("FileUserRepository init failed");
        }
    }

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public ResponseUserDto create(RequestCreateUserDto dto) {
        User user = dto.toEntity();
        userNameIdMap.put(user.getName(), user.getId());
        idUserMap.put(user.getId(), user);
        Path path = resolvePath(user.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            return response(user);
        } catch (IOException e) {
            throw new FailedCreate("FileBinaryContentRepository create failed");
        }
    }

    @Override
    public ResponseUserDto update(RequestUpdateUserDto requestDto) {
        UUID userId = requestDto.id();
        String reName = requestDto.reName();
        String rePassword = requestDto.rePassword();
        String reMail = requestDto.reMail();
        String rePhoneNumber = requestDto.rePhoneNumber();
        UUID reProfileId = requestDto.reProfileId();

        Path path = resolvePath(userId);
        User user = idUserMap.get(userId);

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
            user.updateUser(reName, rePassword, reMail, rePhoneNumber, reProfileId);
            return response(user);
        } catch (IOException e) {
            throw new FailedUpdate("User update failed");
        }
    }

    @Override
    public ResponseUserDto find(String name) {
        User user = idUserMap.get(userNameIdMap.get(name));
        return response(user);
    }

    @Override
    public ResponseUserDto find(UUID userId) {
        User user = idUserMap.getOrDefault(userId, null);
        if(user == null) {
            throw new NotFound("Not Found This User Id");
        }

        return response(user);
    }

    @Override
    public List<ResponseUserDto> findAll() {
        List<ResponseUserDto> result = new ArrayList<>();
        idUserMap.values().stream().sorted(Comparator.comparing(User::getName))
                .forEach(user -> result.add(response(user)));
        return result;
    }

    @Override
    public boolean delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
            userNameIdMap.remove(idUserMap.get(id).getName());
            idUserMap.remove(id);
        } catch (IOException e) {
            throw new FailedDelete("User delete failed");
        }
        return true;
    }

    private ResponseUserDto response(User user) {
        return new ResponseUserDto(user.getId(), user.getCreateAt(), user.getUpdateAt(), user.getName(), user.getEmail(), user.getProfileId());
    }

    @Override
    public boolean isPresent(UUID id) {
        return idUserMap.containsKey(id);
    }

    @Override
    public UUID userNameToId(String name) {
        try {
            return userNameIdMap.get(name);
        } catch (Exception e) {
            throw new NotFound("Do not found this user : " + name);
        }
    }

    @Override
    public String userIdToName(UUID id) {
        try {
            return idUserMap.get(id).getName();
        } catch (Exception e) {
            throw new NotFound("Do not found this user : " + id);
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