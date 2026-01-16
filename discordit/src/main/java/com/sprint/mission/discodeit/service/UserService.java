package com.sprint.mission.discodeit.service;

import java.util.Scanner;

/// 인터페이스를 쓰고자 하는 이유를 생각해보기
/// Test Driven Develope 방식으로 생각하기
/// 이것을 바탕으로 쓰겠다는 청사진을 작성하는 것이다.
/// 설계를 먼저 제대로 하면 인터페이스 수정이 줄어들 것이다.
/// 협업할 때 다른 사람이 이해하기 쉬운 방식으로 구현하기.
///
/// 자바랑 자바스크립트는 별도로 생각하고 구현하기. (변수명 정확히 하기)
/// 묵직하게 자바 스타일로 구현하는 것이 중요하다.
/// Question: 자바 스타일을 배우기 위해 좋은 가이드라인 / 책이 있을지

public interface UserService {
    //    void data();
    void createUser(Scanner sc);

    void updateUser(Scanner sc);

    void getUserName(Scanner sc);

    void getAllUserName();

    void deleteUser(Scanner sc);
}

/// 네 맞아요
/// 협업하는 것을 해본 적이 없어서 이번 과정을 신청해봤고
/// 그래서 피드백 받는 과정이 참 좋네요
/// 근데 진짜 자바스크립트 기반으로 생각하게 돼요
/// 재훈님 은비님 스타일 흡수하는 것이 좋겠다는 피드백을 받음
/// FIXME : 암호화 알고리즘 간단한 것을 적용해보는 것도 생각해보기