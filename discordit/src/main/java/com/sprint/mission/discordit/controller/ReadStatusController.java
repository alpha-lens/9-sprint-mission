package com.sprint.mission.discordit.controller;

import com.sprint.mission.discordit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discordit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discordit.entity.ReadStatus;
import com.sprint.mission.discordit.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/readStatuses")
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ReadStatus> create(@RequestBody ReadStatusCreateRequest request) {
    ReadStatus createdReadStatus = readStatusService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdReadStatus);
  }

  @RequestMapping(path = "{readStatusId}", method = RequestMethod.PATCH)
  public ResponseEntity<ReadStatus> update(@PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updatedReadStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedReadStatus);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<ReadStatus>> findAllByUserId(@RequestParam("userId") UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatuses);
  }
}
