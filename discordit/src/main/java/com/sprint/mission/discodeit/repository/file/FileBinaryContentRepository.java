package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

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

    @Override
    public boolean create(AttachmentType type, UUID id, String file) {
        BinaryContent binaryContent = new BinaryContent(file);
        fileIdMap.put(binaryContent.getId(), binaryContent);
        switch (type){
            case MESSAGE -> messageIdMap.computeIfAbsent(id, m -> new ArrayList<>()).add(binaryContent);
            case CHANNEL -> channelIdMap.put(id, binaryContent);
            case USER -> userIdMap.put(id, binaryContent);
        }
        return true;
    }

    @Override
    public boolean delete(AttachmentType type, UUID id) {
        try{
            switch (type){
                case MESSAGE -> messageIdMap.remove(id);
                case CHANNEL -> channelIdMap.remove(id);
                case USER -> userIdMap.remove(id);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
