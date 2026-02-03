package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> fileIdMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<BinaryContent>> messageIdMap = new ConcurrentHashMap<>();
    private final Map<UUID, BinaryContent> userIdMap = new ConcurrentHashMap<>();
    private final Map<UUID, BinaryContent> channelIdMap = new ConcurrentHashMap<>();
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private Path resolvePath(UUID id) {
        String EXTENSION = ".ser";
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", BinaryContent.class.getSimpleName());
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
                            return (BinaryContent) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(binaryContent -> {
                        switch (binaryContent.getType()){
                            case USER -> userIdMap.put(binaryContent.getRelationId(), binaryContent);
                            case CHANNEL -> channelIdMap.put(binaryContent.getRelationId(), binaryContent);
                            case MESSAGE -> messageIdMap.computeIfAbsent(binaryContent.getId(), id -> new ArrayList<>()).add(binaryContent);
                        }
                    });
        } catch (Exception e) {
            System.err.println("[ERROR] : " + e);
        }
    }

    @Override
    public boolean create(AttachmentType type, UUID id, String file) {
        BinaryContent binaryContent = new BinaryContent(type, id, file);
        fileIdMap.put(binaryContent.getId(), binaryContent);
        switch (type){
            case MESSAGE -> messageIdMap.computeIfAbsent(id, m -> new ArrayList<>()).add(binaryContent);
            case CHANNEL -> channelIdMap.put(id, binaryContent);
            case USER -> userIdMap.put(id, binaryContent);
        }

        Path path = resolvePath(binaryContent.getId());

        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            System.err.println("파일 생성에 실패했다 : " + e);
            return false;
        }

        return true;
    }

    @Override
    public List<String> find(AttachmentType type, UUID id){
        try {
            switch (type){
                case MESSAGE -> {
                    return messageIdMap.get(id).stream().map(BinaryContent::toString).toList();
                }
                case CHANNEL -> {
                    return List.of(channelIdMap.get(id).toString());
                }
                case USER -> {
                    return List.of(userIdMap.get(id).toString());
                }
            }
        } catch (Exception e) {
            throw new NotFound("왠지 모르지만 오류가 발생했다!");
        }
        return List.of();
    }

    @Override
    public boolean delete(AttachmentType type, UUID id) {
        try {
            switch (type){
                case MESSAGE -> {
                    messageIdMap.remove(id).forEach(t -> {
                        delete(t.getId());
                    });
                }
                case CHANNEL -> {
                    delete(channelIdMap.remove(id).getId());
                }
                case USER -> {
                    delete(userIdMap.remove(id).getId());
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void delete(UUID id) {
        try {
            Path path = resolvePath(id);
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
