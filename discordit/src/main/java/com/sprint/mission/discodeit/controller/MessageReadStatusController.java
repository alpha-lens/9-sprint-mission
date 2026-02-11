package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestCreateReadStatusDto;
import com.sprint.mission.discodeit.dto.response.ResponseReadStatus;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readstatus")
@RequiredArgsConstructor
public class MessageReadStatusController {
    private final ReadStatusService readStatusService;
    private final ChannelService channelService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public List<ResponseReadStatus> handleCreateReadStatus(
            @RequestParam("userId") UUID userId
    ) {
        List<UUID> channelIds = new ArrayList<>();

        for(ResponseChannelDto dto : channelService.findAll(userId)) {
            channelIds.add(dto.channelId());
        }

        return readStatusService.create(new RequestCreateReadStatusDto(userId, channelIds));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseReadStatus handleUpdateReadStatus(
            @RequestParam("userId") UUID userId,
            @RequestParam("channelId") UUID channelId
    ) {
        return readStatusService.update(userId, channelId);
    }

    @RequestMapping(value = "/find/{userId}", method = RequestMethod.GET)
    public List<ResponseReadStatus> handleFindAllReadStatusForUser(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}


/*
* 메시지 수신 정보 관리
* [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
* [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
* [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
* */