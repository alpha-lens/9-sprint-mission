| 엔티티 관계                  | 다중성   | 방향성                          | 부모-자식 관계                        | 연관관계 주인    |
|-------------------------|-------|------------------------------|---------------------------------|------------|
| ReadStatus : Channel    | N : 1 | ReadStatus -> Channel 단방향    | 부모 : Channel 자식 : ReadStatus    | ReadStatus |
| ReadStatus : User       | N : 1 | ReadStatus -> User 단방향       | 부모 : User 자식 : ReadStatus       | ReadStatus |
| User : UserStatus       | 1 : 1 | User <-> UserStatus 양방향      | 부모 : User 자식 : UserStatus       | UserStatus |
| User : BinaryContent    | 1 : 1 | User -> BinaryContent 단방향    | 부모 : User 자식 : BinaryContent    | User       |
| Message : Channel       | N : 1 | Message -> Channel 단방향       | 부모 : Channel 자식 : Message       | Message    |
| Message : User          | N : 1 | Message -> User 단방향          | 부모 : User 자식 : Message          | Message    |
| Message : BinaryContent | 1 : N | Message -> BinaryContent 단방향 | 부모 : Message 자식 : BinaryContent | Message    |
