package com.sprint.mission.discodeit.app.router;

import com.sprint.mission.discodeit.app.JavaApplication;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class RouteChannelService {
    private final BasicChannelService channelService;
    private final Scanner sc;

    public void channelService(int routeCRUD) {
        int menu;

        switch (routeCRUD) {

            /// create
            case 1:
                channelService.createChannel();
                break;

            /// update
            case 2:
                channelService.updateChannel();
                break;

            /// read
            case 3:
                System.out.println("전체 사용자 정보를 가져올까요?");
                System.out.println("1 : 특정 채널만 가져옵니다");
                System.out.println("2 : 전체 채널을 가져옵니다");

                // TODO:try-catch로 안정성 확보하기
                menu = sc.nextInt();
                sc.nextLine();

                if (menu == 1) channelService.readChannel();
                else if (menu == 2) channelService.readAllChannel();
                break;

            /// delete
            case 4:
                channelService.deleteChannel();
                break;

            default:
                return;
        }
    }
}
