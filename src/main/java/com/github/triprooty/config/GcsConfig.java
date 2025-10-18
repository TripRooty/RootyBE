package com.github.triprooty.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class GcsConfig {
    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.credentials.location}")
    private Resource credentialsLocation;

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(
                        GoogleCredentials.fromStream(credentialsLocation.getInputStream())
                )
                .build()
                .getService();
    }
}
