package com.example.demo.queue;

import java.time.Duration;

import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;
import com.example.demo.configuration.AzureQueueSyncServiceAClientConfig;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class QueueProcess {

    AzureQueueSyncServiceAClientConfig azureQueueConfig;

    public QueueProcess(AzureQueueSyncServiceAClientConfig azureQueueConfig){
        this.azureQueueConfig = azureQueueConfig;
    }

    public void addQueueMessage(String accountName,String accountKey, String queueName, String messageText){
        try{
            
            QueueClient queueClient = new QueueClientBuilder()
                                        .connectionString("DefaultEndpointsProtocol=https;" +
                                        "AccountName="+ accountName + ";" +
                                        "AccountKey=" + accountKey)
                                        .queueName("already-generated-queue")
                                        .queueName(queueName)
                                        .buildClient();

            log.info("Adding message to the queue: " + messageText);

            queueClient.sendMessage(messageText);
        }
        catch (QueueStorageException e){
            log.info("Fatal Error");
        }
    }


    public void dequeueMessages(String accountName, String accountKey, String queueName){
        try{

            QueueClient queueGeneratedClient = new QueueClientBuilder()
            .connectionString("DefaultEndpointsProtocol=https;" +
            "AccountName="+ accountName + ";" +
            "AccountKey=" + accountKey)
            .queueName(queueName)
            .buildClient();

            QueueMessageItem queueMessageItem = queueGeneratedClient.receiveMessage();
            log.info("Dequeing message --> " + queueMessageItem.getMessageId() + " " + queueMessageItem.getBody().toString());
            queueGeneratedClient.deleteMessage(queueMessageItem.getMessageId(), queueMessageItem.getPopReceipt());

        }

        catch (QueueStorageException e){
            log.info("No messages");
        }
    
    }

    public void updateQueueMessage(QueueClient queueClient,
    String searchString, String updatedContents){
        try{

            final int MAX_MESSAGES = 32;

            for (QueueMessageItem message : queueClient.receiveMessages(MAX_MESSAGES))
            {

                if (message.getBody().toString().equals(searchString)){
                    
                    queueClient.updateMessage(message.getMessageId(),
                                            message.getPopReceipt(),
                                            updatedContents,
                                            Duration.ofSeconds(10));
                    log.info( String.format("Found message: \'%s\' and updated it to \'%s\'",
                                searchString,
                                updatedContents)
                                                );
                }
            }
        }
        catch (QueueStorageException e){

            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

}
