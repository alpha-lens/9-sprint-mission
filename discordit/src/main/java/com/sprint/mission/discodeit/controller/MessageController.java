package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
}

/*
* 메시지 관리
* [ ] 메시지를 보낼 수 있다.
* [ ] 메시지를 수정할 수 있다.
* [ ] 메시지를 삭제할 수 있다.
* [ ] 특정 채널의 메시지 목록을 조회할 수 있다.
*/