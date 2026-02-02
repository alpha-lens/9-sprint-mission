package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

//    public boolean create(String filename) {
//        binaryContentRepository;
//        return true;
//    }
}
