package com.sprint.mission.discodeit.storage.s3;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AWSS3Test {

  private S3Client s3Client;
  private S3Presigner presigner;
  private String bucketName;
  private final String testKey = "test-file.txt";

  @BeforeAll
  void setUp() {
    // 1. .env 로드 및 Properties 변환
    Dotenv dotenv = Dotenv.load();
    Properties props = new Properties();
    props.setProperty("accessKey", dotenv.get("AWS_S3_ACCESS_KEY"));
    props.setProperty("secretKey", dotenv.get("AWS_S3_SECRET_KEY"));
    props.setProperty("region", dotenv.get("AWS_S3_REGION"));
    this.bucketName = dotenv.get("AWS_S3_BUCKET");

    // 2. S3 클라이언트 초기화
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        props.getProperty("accessKey"),
        props.getProperty("secretKey")
    );

    Region region = Region.of(props.getProperty("region"));

    this.s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    this.presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  @Test
  @DisplayName("S3 파일 업로드 테스트")
  void uploadTest() {
    PutObjectRequest putOb = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(testKey)
        .contentType("text/plain")
        .build();

    PutObjectResponse response = s3Client.putObject(putOb,
        RequestBody.fromString("Hello S3! This is a test file."));

    Assertions.assertNotNull(response.eTag());
  }

  @Test
  @DisplayName("S3 파일 다운로드 테스트")
  void downloadTest() {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testKey)
        .build();

    ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
    String content = objectBytes.asString(StandardCharsets.UTF_8);

    Assertions.assertTrue(content.contains("Hello S3"));
  }

  @Test
  @DisplayName("Presigned URL 생성 테스트")
  void generatePresignedUrlTest() {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(testKey)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10)) // 10분간 유효
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
    String url = presignedRequest.url().toString();

    System.out.println("Generated Presigned URL: " + url);
    Assertions.assertNotNull(url);
    Assertions.assertTrue(url.contains(bucketName));
  }

  @AfterAll
  void tearDown() {
    s3Client.close();
    presigner.close();
  }
}