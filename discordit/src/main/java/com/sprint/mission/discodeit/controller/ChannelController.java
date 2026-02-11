package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RequestUpdateChannelDto;
import com.sprint.mission.discodeit.dto.response.ResponseChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exepction.global.Forbidden;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;

    @RequestMapping(value="/create", method= RequestMethod.POST)
    public ResponseChannelDto handleCreatePublicChannel(
            @RequestParam("channelName") String channelName,
            @RequestParam("channelType") String type,
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        return channelService.create(type, channelName, userId);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public List<ResponseChannelDto> findChannel(
            @RequestParam("id") UUID userId
    ){
        return channelService.findAll(userId);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseChannelDto handleUpdateChannel(
            @RequestParam("channelId") UUID channelId,
            @RequestParam("userId") UUID userId,
            @RequestParam("newChannelName") String newChannelName
    ) {
        String userName = userService.find(userId).username();

        if(!channelService.isPresent(channelId)){
            throw new NotFound("The channel does not exist.");
        }

        if(channelService.find(channelId, userId).channelType() == ChannelType.PRIVATE){
            throw new Forbidden("Failed: Private channel cannot update!");
        }

        if(!channelService.findChannelCreator(channelId, userName))
            throw new Forbidden("Channel update is only creator!");

        return channelService.update(new RequestUpdateChannelDto(channelId, newChannelName));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<String> handleDeleteChannel(
            @RequestParam("channelId") UUID channelId,
            @RequestParam("userId") UUID userId
    ) {
        String userName = userService.find(userId).username();

        if(!channelService.isPresent(channelId)){
            throw new NotFound("The channel does not exist.");
        }

        if(!channelService.findChannelCreator(channelId, userName)) {
            throw new Forbidden("Failed: Private channel cannot update!");
        }

        channelService.delete(channelId);
        return new ResponseEntity<>("Success: " + channelId + " has been deleted.", HttpStatus.OK);
    }
}