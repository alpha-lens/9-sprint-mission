package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.AttechmentRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AttachmentRepository implements AttechmentRepository {
    private final Map<UUID, BinaryContent> fileIdMap = new ConcurrentHashMap<>();private final Map<UUID, BinaryContent> relationIdMap = new ConcurrentHashMap<>();
    public boolean create(UUID id, String file) {
        BinaryContent binaryContent = new BinaryContent(file);
        fileIdMap.put(binaryContent.getId(), binaryContent);
        relationIdMap.put(id, binaryContent);
        return true;
    }

    public boolean delete(UUID relationId) {
        try{
            relationIdMap.remove(relationId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getAttachementFile(UUID id) {
        return fileIdMap.get(id).getFileName();
    }
}
