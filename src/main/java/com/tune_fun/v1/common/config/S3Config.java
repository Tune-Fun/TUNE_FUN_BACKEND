package com.tune_fun.v1.common.config;

import com.tune_fun.v1.common.property.AwsProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Profile("!dev_standalone && !test_standalone")
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsProperty awsProperty;

    @Bean
    public S3Client amazonS3Client() {
        return S3Client.builder()
                .region(Region.of(awsProperty.getDefaultRegion()))
                .credentialsProvider(getS3CredentialProvider())
                .build();
    }

    private AwsCredentialsProvider getS3CredentialProvider() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsProperty.getAccessKeyId(), awsProperty.getSecretAccessKey());
        return StaticCredentialsProvider.create(credentials);
    }


}
