package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final Scanner scanner;
    private final FileReadStatusRepository readStatusRepository;

    public void create(UUID userId, UUID channelId){
        readStatusRepository.create(userId, channelId);
    }
    public void find(UUID id) {
        Instant lastReadAt = readStatusRepository.find(id);
    }
    public void findAllByUserId(UUID userId) {
        Map<UUID, Instant> result = readStatusRepository.findAllByUserId(userId);
    }
    public void update(UUID userId, UUID channelId) {

    }
    public void delete(UUID id) {}
}
