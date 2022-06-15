package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;

@Configuration
public class AzureQueueSyncServiceAClientConfig {

    @Value("${spring.cloud.azure.storage.blob.account-name}")
    private String accountName;

    @Value("${spring.cloud.azure.storage.blob.account-key}")
    private String accountKey;

    @Bean
    QueueClient getClient() {
        return new QueueClientBuilder()
                .connectionString("DefaultEndpointsProtocol=https;" +
                        "AccountName=" + accountName + ";" +
                        "AccountKey=" + accountKey)
                .queueName("already-generated-queue")
                .buildClient();
    } 

    public String getAccountName() {
        return accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }

}
