package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class FileReadStatusRepository implements ReadStatusRepository {
    // TODO: ID:ReadStatus
    // TODO: UserId:ReadStatus
    private final Map<UUID, ReadStatus> idReadStatusMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<ReadStatus>> userIdReadStatusMap = new ConcurrentHashMap<>();
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", ReadStatus.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.list(DIRECTORY).filter(path-> path.toString().endsWith(EXTENSION))
                    .map(path->{
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (ReadStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(readstatus -> {
                        idReadStatusMap.put(readstatus.getId(), readstatus);
                        userIdReadStatusMap.computeIfAbsent(readstatus.getUserId(), id -> new ArrayList<>()).add(readstatus);
                    });
        } catch (Exception e) {
            System.err.println("[ERROR] : " + e);
        }
    }

    public void create(UUID userId, UUID channelId) {
        ReadStatus temp = new ReadStatus(userId, channelId);
        idReadStatusMap.put(temp.getId(), temp);
        userIdReadStatusMap.computeIfAbsent(userId, id -> new ArrayList<>()).add(temp);
        Path path = resolvePath(temp.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(temp);
        } catch (IOException e) {
            System.err.println("[ERROR] : " + e);
        }
    }

    public Instant find(UUID id) {
        return idReadStatusMap.get(id).getLastReadAt();
    }

    public Map<UUID, Instant> findAllByUserId(UUID userId) {
        List<ReadStatus> tempList = userIdReadStatusMap.get(userId);
        Map<UUID, Instant> result = new HashMap<>();
        for(ReadStatus temp : tempList) {
            result.put(temp.getChannelId(), temp.getLastReadAt());
        }

        return result;
    }

    public void update(UUID id) {
        ReadStatus temp = idReadStatusMap.get(id);
        Path path = resolvePath(id);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(temp);
        } catch (IOException e) {
            System.err.println("[ERROR] : " + e);
        }
    }

    public boolean delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
            userIdReadStatusMap.remove(idReadStatusMap.remove(id).getUserId());
            return true;
        } catch (IOException e) {
            System.err.println("[ERROR] : "+ e);
            return false;
        }
    }
}
