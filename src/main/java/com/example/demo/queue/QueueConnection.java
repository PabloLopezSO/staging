package com.example.demo.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QueueConnection {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    private String azureStorageConnection = 
    "DefaultEndpointsProtocol=https;" +
    "AccountName="+ accountName +";" +
    "AccountKey="+accountKey;

    public String getAzureStorageConnection() {
        return azureStorageConnection;
    }
}
