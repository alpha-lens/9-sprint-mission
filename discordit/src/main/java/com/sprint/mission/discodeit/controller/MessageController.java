package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.CreateBinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateMessageDto;
import com.sprint.mission.discodeit.dto.apiresponse.ResponseMessage;
import com.sprint.mission.discodeit.entity.AttachmentType;
import com.sprint.mission.discodeit.exepction.NotFound;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    private final BasicBinaryContentService binaryContentService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final UserService userService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseMessage handleCreateMessage(
            @RequestParam("channelId") UUID channelId,
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
            UUID messageId = messageService.create(requestMessageDto);
            return new ResponseMessage(messageId, binaryContentIds, content);
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String extension = fileName.substring(fileName.lastIndexOf("."));
            if(List.of("jpg", "jpeg", "png").contains(extension)) {
                byte[] bytes = file.getBytes();
                CreateBinaryContentDto requestBinaryContentDto = new CreateBinaryContentDto(AttachmentType.MESSAGE, file.getOriginalFilename(), bytes);
                binaryContentIds.add(binaryContentService.create(requestBinaryContentDto));
            }
        }

        CreateMessageDto requestMessageDto = new CreateMessageDto(content, channelId, userId, binaryContentIds);
        UUID messageId = messageService.create(requestMessageDto);

        return new ResponseMessage(messageId, binaryContentIds, content);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public ResponseMessage handleUpdateMessage(
            @PathVariable("id") UUID messageId,
            @RequestParam("userId") UUID userId,
            @RequestParam("new_content") String newContent
    ) {
        return messageService.update(userId, messageId, newContent);
    }
}

/*
* 메시지 관리
* [ ] 메시지를 보낼 수 있다.
* [ ] 메시지를 수정할 수 있다.
* [ ] 메시지를 삭제할 수 있다.
* [ ] 특정 채널의 메시지 목록을 조회할 수 있다.
*/