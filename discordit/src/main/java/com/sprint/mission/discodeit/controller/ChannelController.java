package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.UserState;
import com.sprint.mission.discodeit.dto.UpdateChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
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

import java.util.UUID;

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

//    @RequestMapping(value = "/find", method = RequestMethod.GET)
//    public ResponseEntity<String> findChannel(
//            @RequestParam("id") UUID id
//    ){
//
//    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<String> handleUpdateChannel(
            @RequestParam("oldChannelName") String oldChannelName,
            @RequestParam("newChannelName") String newChannelName
    ) {
        if(channelService.find(oldChannelName).channelType() == ChannelType.PRIVATE){
            return new ResponseEntity<>("Failed: Private channel cannot update!", HttpStatus.BAD_REQUEST);
        }

        if(!channelService.isPresent(oldChannelName)){
            return new ResponseEntity<>("Failed: This channel not found.", HttpStatus.BAD_REQUEST);
        }

        channelService.update(new UpdateChannelDto(oldChannelName, newChannelName));
        return new ResponseEntity<>("Success: " + oldChannelName + " -> " + newChannelName + " changed.", HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> handleDeleteChannel(
            @RequestParam("channelName") String channelName
    ) {
        String userName = userState.getUserName();

        if(!channelService.isPresent(channelName)){
            return new ResponseEntity<>("Failed: This channel not found.", HttpStatus.BAD_REQUEST);
        }

        if(!channelService.findChannelCreator(channelName, userName)) {
            return new ResponseEntity<>("Failed: You didn't create this channel", HttpStatus.UNAUTHORIZED);
        }

        channelService.delete(channelName);
        return new ResponseEntity<>("Success: " + channelName + " has been deleted.", HttpStatus.OK);
    }
}

/*
* 채널 관리
* [X] 공개 채널을 생성할 수 있다.
* [X] 비공개 채널을 생성할 수 있다.
* [X] 공개 채널의 정보를 수정할 수 있다.
* [X] 채널을 삭제할 수 있다.
* [ ] 특정 사용자가 볼 수 있는 모든 채널 목록을 조회할 수 있다.
* */