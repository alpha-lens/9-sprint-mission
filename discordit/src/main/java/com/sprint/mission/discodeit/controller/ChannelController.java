package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.checker.CheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final UserState userState;
    private final CheckService checkService;

    @RequestMapping(value="/create/public", method= RequestMethod.POST)
    public ResponseEntity<String> handleCreatePublicChannel(
            @RequestParam("channelName") String channelName,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if(checkService.isNotLogin()){
            return new ResponseEntity<>("Failed: You can access it after you log in.", HttpStatus.UNAUTHORIZED);
        }

        channelService.create("public", channelName, userState.getUserName());
        return new ResponseEntity<>("Success: " + channelName + " public channel has been created!", HttpStatus.OK);
    }

    @RequestMapping(value="/create/private", method= RequestMethod.POST)
    public ResponseEntity<String> handleCreatePrivateChannel(
            @RequestParam("channelName") String channelName,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if(checkService.isNotLogin()){
            return new ResponseEntity<>("Failed: You can access it after you log in.", HttpStatus.UNAUTHORIZED);
        }

        channelService.create("private", channelName, userState.getUserName());
        return new ResponseEntity<>("Success: " + channelName + " private channel has been created!", HttpStatus.OK);
    }
}

/*
* 채널 관리
* [X] 공개 채널을 생성할 수 있다.
* [X] 비공개 채널을 생성할 수 있다.
* [ ] 공개 채널의 정보를 수정할 수 있다.
* [ ] 채널을 삭제할 수 있다.
* [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
* */