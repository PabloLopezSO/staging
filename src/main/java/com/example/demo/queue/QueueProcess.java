package com.example.demo.queue;

import com.azure.core.util.*;
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class QueueProcess {

    public static String createQueue(String queueConnection){
        try{

            /* String queueName = "queue-" + java.util.UUID.randomUUID(); */
            String queueName = "thumbnail-generator";

            log.info("Creating queue: " + queueName);

            QueueClient queue = new QueueClientBuilder()
                                    .connectionString(queueConnection)
                                    .queueName(queueName)
                                    .buildClient();

            queue.create();
            return queue.getQueueName();
        }
        catch (QueueStorageException e){
            log.info("Error code: " + e.getErrorCode() + "Message: " + e.getMessage());
            return null;
        }
    }

    public static void addQueueMessage(String queueConnection, String queueName, String messageTextQueue){
        try{
            QueueClient queueClient = new QueueClientBuilder()
                                        .connectionString(queueConnection)
                                        .queueName(queueName)
                                        .buildClient();

            log.info("Adding message to the queue: " + messageTextQueue);

            queueClient.sendMessage(messageTextQueue);
        }
        catch (QueueStorageException e){
            
            log.info(e.getMessage());
            e.printStackTrace();
        }
}
    
}
