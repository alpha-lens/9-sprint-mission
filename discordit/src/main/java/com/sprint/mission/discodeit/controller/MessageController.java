package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateMessageDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.exepction.global.NotFound;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/{channelId}/message")
@RequiredArgsConstructor
public class MessageController {
    private final BasicBinaryContentService binaryContentService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final UserService userService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public MessageResponseDto handleCreateMessage(
            @PathVariable UUID channelId,
            @RequestParam("userId") UUID userId,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) throws IOException {
        List<UUID> binaryContentIds = new ArrayList<>();

        if(!channelService.isPresent(channelId))
            throw new NotFound("This channel is not present");

        if(!userService.isPresent(userId))
            throw new NotFound("This user is not present");

        if(files == null || files.isEmpty()){
            CreateMessageDto requestMessageDto = new CreateMessageDto(content, channelId, userId, null);
            return messageService.create(requestMessageDto);
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            if(List.of(".jpg", ".jpeg", ".png").contains(extension)) {
                byte[] bytes = file.getBytes();
                CreateBinaryContentDto requestBinaryContentDto = new CreateBinaryContentDto(AttachmentType.MESSAGE, file.getOriginalFilename(), bytes);
                binaryContentIds.add(binaryContentService.create(requestBinaryContentDto));
            }
        }

        CreateMessageDto requestMessageDto = new CreateMessageDto(content, channelId, userId, binaryContentIds);
        return messageService.create(requestMessageDto);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public MessageResponseDto handleUpdateMessage(
            @PathVariable("id") UUID messageId,
            @RequestParam("userId") UUID userId,
            @RequestParam("new_content") String newContent
    ) {
        return messageService.update(userId, messageId, newContent);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> handleDeleteMessage(
            @PathVariable("id") UUID messageId,
            @RequestParam("userId") UUID userId
    ) {
        if (messageService.delete(userId, messageId)) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @RequestMapping(value = "/find")
    public List<MessageResponseDto> handleFindMessage(
            @PathVariable UUID channelId,
            @RequestParam("userId") UUID userId
    ) {
        channelService.find(channelId, userId);
        return messageService.findAllInChannel(channelId);
    }
}

/*
* 메시지 관리
* [X] 메시지를 보낼 수 있다.
* [X] 메시지를 수정할 수 있다.
* [ ] 메시지를 삭제할 수 있다.
* [ ] 특정 채널의 메시지 목록을 조회할 수 있다.
*/