-- 1. UUID 생성을 위한 확장 모듈 활성화 (필요한 경우)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. ENUM 타입 정의 (channels 테이블의 type 컬럼용)
CREATE TYPE channel_type_enum AS ENUM ('PUBLIC', 'PRIVATE');

-- 3. binary_contents 테이블 (users보다 먼저 생성되어야 함 - profile_id 참조 때문)
CREATE TABLE binary_contents (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 file_name VARCHAR(255) NOT NULL,
                                 size BIGINT NOT NULL,
                                 content_type VARCHAR(100) NOT NULL,
                                 bytes BYTEA NOT NULL -- 실제 파일 바이너리 데이터
);

-- 4. users 테이블
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       password VARCHAR(60) NOT NULL, -- 아마도 BCrypt 해시 저장용
                       profile_id UUID,

                       CONSTRAINT uq_users_username UNIQUE (username),
                       CONSTRAINT uq_users_email UNIQUE (email),
                       CONSTRAINT uq_users_profile_id UNIQUE (profile_id), -- 1:1 관계 보장
                       CONSTRAINT fk_users_profile FOREIGN KEY (profile_id)
                           REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- 5. channels 테이블
CREATE TABLE channels (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ,
                          name VARCHAR(100),
                          description VARCHAR(500),
                          type channel_type_enum NOT NULL DEFAULT 'PUBLIC'
);

-- 6. user_statuses 테이블
CREATE TABLE user_statuses (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL,
                               last_active_at TIMESTAMPTZ NOT NULL,

                               CONSTRAINT uq_user_statuses_user_id UNIQUE (user_id), -- 1:1 관계 보장
                               CONSTRAINT fk_user_statuses_user FOREIGN KEY (user_id)
                                   REFERENCES users(id) ON DELETE CASCADE
);

-- 7. messages 테이블
CREATE TABLE messages (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ,
                          content TEXT, -- 긴 텍스트 가능성 고려
                          channel_id UUID NOT NULL,
                          author_id UUID, -- 탈퇴한 회원의 메시지 보존을 위해 NULL 허용으로 보임

                          CONSTRAINT fk_messages_channel FOREIGN KEY (channel_id)
                              REFERENCES channels(id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_author FOREIGN KEY (author_id)
                              REFERENCES users(id) ON DELETE SET NULL
);

-- 8. read_statuses 테이블
CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ,
                               user_id UUID NOT NULL,
                               channel_id UUID NOT NULL,
                               last_read_at TIMESTAMPTZ NOT NULL,

    -- 한 유저가 한 채널에 대해 하나의 읽음 상태만 가짐
                               CONSTRAINT uq_read_statuses_user_channel UNIQUE (user_id, channel_id),
                               CONSTRAINT fk_read_statuses_user FOREIGN KEY (user_id)
                                   REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT fk_read_statuses_channel FOREIGN KEY (channel_id)
                                   REFERENCES channels(id) ON DELETE CASCADE
);

-- 9. message_attachments 테이블 (다대다 해소 테이블)
CREATE TABLE message_attachments (
                                     message_id UUID NOT NULL,
                                     attachment_id UUID NOT NULL,

                                     PRIMARY KEY (message_id, attachment_id),
                                     CONSTRAINT fk_ma_message FOREIGN KEY (message_id)
                                         REFERENCES messages(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_ma_attachment FOREIGN KEY (attachment_id)
                                         REFERENCES binary_contents(id) ON DELETE CASCADE
);

-- 보너스: updated_at 자동 갱신을 위한 함수 및 트리거 (선택 사항)
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
RETURN NEW;
END;
$$ language 'plpgsql';

-- 예시: users 테이블에 트리거 적용
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_updated_at_column();
-- (나머지 테이블에도 동일하게 적용 가능)