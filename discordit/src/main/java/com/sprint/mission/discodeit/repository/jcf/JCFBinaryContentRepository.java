package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exepction.FailedFound;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> fileIdMap = new ConcurrentHashMap<>();

    @Override
    public UUID create(CreateBinaryContentDto requestDto) {
        AttachmentType type = requestDto.type();
        String file = requestDto.filename();
        byte[] bytes = requestDto.bytes();
        BinaryContent binaryContent = new BinaryContent(type, file, bytes);

        fileIdMap.put(binaryContent.getId(), binaryContent);

        return binaryContent.getId();
    }

    @Override
    public String find(UUID id) {
        try {
            return fileIdMap.get(id).toString();
        } catch (Exception e) {
            throw new FailedFound("Binary content not found");
        }
    }

    @Override
    public List<String> findAllByIdIn(List<UUID> ids) {
        List<String> result = new ArrayList<>();
        ids.forEach(id -> result.add(fileIdMap.get(id).toString()));
        return result;
    }

    @Override
    public boolean delete(UUID id) {
        fileIdMap.remove(id);
        return true;
    }

    @Override
    public void delete(List<UUID> ids) {
        ids.forEach(this::delete);
    }
}
