package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binarycontent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BasicBinaryContentService basicBinaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public List<ResponseBinaryContentDto> handleFindBinaryContents(
            @RequestParam("binaryContentIds") List<UUID> binaryContentIds
    ) {
        return basicBinaryContentService.findAllByIdIn(binaryContentIds);
    }
}

/*
* 바이너리 파일 다운로드
* [ ] 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
* */