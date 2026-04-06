FROM amazoncorretto:17

WORKDIR /app

# 환경 변수 설정
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8
ENV JVM_OPTS=""

# 1. 빌드에 필요한 최소 파일만 복사 (캐싱 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# 2. 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# 3. 요구사항대로 80 포트 노출
EXPOSE 80

# 4. 실행 명령어 (포트 80에서 실행되도록 서버 포트 지정 추가 권장)
ENTRYPOINT ["sh", "-c", "java ${JVM_OPTS} -Dserver.port=80 -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]