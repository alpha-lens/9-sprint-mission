package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.ResponseChannelDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
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
    public List<ReadStatusResponse> handleCreateReadStatus(
            @RequestParam("userId") UUID userId
    ) {
        List<UUID> channelIds = new ArrayList<>();

        for(ResponseChannelDto dto : channelService.findAll(userId)) {
            channelIds.add(dto.channelId());
        }

        return readStatusService.create(new ReadStatusCreateRequest(userId, channelIds));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ReadStatusResponse handleUpdateReadStatus(
            @RequestParam("userId") UUID userId,
            @RequestParam("channelId") UUID channelId
    ) {
        return readStatusService.update(userId, channelId);
    }

    @RequestMapping(value = "/find/{userId}", method = RequestMethod.GET)
    public List<ReadStatusResponse> handleFindAllReadStatusForUser(@PathVariable UUID userId) {
        return readStatusService.findAllByUserId(userId);
    }
}


/*
* 메시지 수신 정보 관리
* [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
* [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
* [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
* */