package com.project.dogwalker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {

  @Value("${aws.s3.accessKeyId}")
  private String AWS_ACCESS_KEY;

  @Value("${aws.s3.secretKey}")
  private String AWS_SECRET_KEY;

  @Value("${aws.s3.region}")
  private String REGION;


  @Bean
  public S3Client amazonS3Client(){
    return S3Client.builder()
              .region(Region.of(REGION))
              .credentialsProvider(
                  StaticCredentialsProvider.create(AwsBasicCredentials.create(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
              .build();
  }
}
