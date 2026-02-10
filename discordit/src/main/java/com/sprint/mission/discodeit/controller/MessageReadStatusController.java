package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/message/readstatus")
@RequiredArgsConstructor
public class MessageReadStatusController {
}


/*
* 메시지 수신 정보 관리
* [ ] 특정 채널의 메시지 수신 정보를 생성할 수 있다.
* [ ] 특정 채널의 메시지 수신 정보를 수정할 수 있다.
* [ ] 특정 사용자의 메시지 수신 정보를 조회할 수 있다.
* */