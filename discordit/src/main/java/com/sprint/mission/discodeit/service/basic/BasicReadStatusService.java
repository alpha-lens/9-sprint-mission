package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final ChannelRepository channelRepository;

    @Override
    public List<ReadStatusResponse> create(ReadStatusCreateRequest request){
        return readStatusRepository.create(request);
    }

    @Override
    public Instant find(UUID id) {
        return readStatusRepository.find(id);
    }
    @Override
    public List<ReadStatusResponse> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }
    @Override
    public ReadStatusResponse update(UUID userId, UUID channelId) {
        return readStatusRepository.update(userId, channelId);
    }
    @Override
    public void deleteForChannel(UUID channelId) {
        readStatusRepository.deleteForChannel(channelId);
    }
    @Override
    public void deleteForUser(UUID userId) {
        readStatusRepository.deleteForUser(userId);
    }
}
