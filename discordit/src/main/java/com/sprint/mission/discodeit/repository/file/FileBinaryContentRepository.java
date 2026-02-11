package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.dto.request.RequestCreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.exepction.FailedCreate;
import com.sprint.mission.discodeit.exepction.FailedDelete;
import com.sprint.mission.discodeit.exepction.FailedInit;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
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
@Profile("file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> fileIdMap = new ConcurrentHashMap<>();
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
                        fileIdMap.put(binaryContent.getId(), binaryContent);
                    });
        } catch (Exception e) {
            throw new FailedInit("FileBinaryContentRepository init failed");
        }
    }

    @Override
    public UUID create(RequestCreateBinaryContentDto requestDto) {
        String type = requestDto.contentType();
        String file = requestDto.filename();
        BinaryContent binaryContent = new BinaryContent(type, file, null);
        
        Path path = resolvePath(binaryContent.getId());
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(binaryContent);
        } catch (IOException e) {
            throw new FailedCreate("파일 생성에 실패했습니다.");
        }

        fileIdMap.put(binaryContent.getId(), binaryContent);

        return binaryContent.getId();
    }

    @Override
    public ResponseBinaryContentDto find(UUID id) {
        return response(fileIdMap.get(id));
    }

    @Override
    public List<ResponseBinaryContentDto> findAllByIdIn(List<UUID> ids) {
        List<ResponseBinaryContentDto> result = new ArrayList<>();
        ids.forEach(id -> result.add(response(fileIdMap.get(id))));
        return result;
    }

    private ResponseBinaryContentDto response(BinaryContent binaryContent) {
        return new ResponseBinaryContentDto(
                binaryContent.getId(),
                binaryContent.getCreateAt(),
                binaryContent.getFileName(),
                binaryContent.getFileExtension(),
                binaryContent.getType(),
                binaryContent.getBytes()
        );
    }

    @Override
    public boolean delete(UUID id) {
        try {
            Path path = resolvePath(id);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FailedDelete("IOException 발생");
        }

        fileIdMap.remove(id);
        return true;
    }

    @Override
    public void delete(List<UUID> ids) {
        ids.forEach(this::delete);
    }
}
