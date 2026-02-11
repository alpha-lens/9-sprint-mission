package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.ResponseBinaryContentDto;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-content")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BasicBinaryContentService basicBinaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public List<ResponseBinaryContentDto> handleFindBinaryContents(
            @RequestParam("binaryContentId") List<UUID> binaryContentId
    ) {
        return basicBinaryContentService.findAllByIdIn(binaryContentId);
    }
}