package com.example.demo.queue;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueMessageItem;
import com.azure.storage.queue.models.QueueStorageException;
import com.example.demo.configuration.AzureQueueSyncServiceAClientConfig;
import com.example.demo.domain.Task;
import com.example.demo.exception.InvalidParamException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.FileService;
import com.example.demo.domain.FileServiceEnum;

import org.json.JSONObject;
import org.json.JSONArray;  

import lombok.extern.log4j.Log4j2;

@EnableScheduling
@Component
@Log4j2
public class QueuePooling {

    private final QueueClient queueClient;
    private TaskRepository taskRepository;
    AzureQueueSyncServiceAClientConfig azureQueueConfig;


    @Autowired
    public QueuePooling(QueueClient queueClient, AzureQueueSyncServiceAClientConfig azureQueueConfig, TaskRepository taskRepository){
        this.queueClient = queueClient;
        this.azureQueueConfig = azureQueueConfig;
        this.taskRepository = taskRepository;
    }

    public String receiveMessages(){

        StringBuilder queueMessages= new StringBuilder();

        for (QueueMessageItem message : queueClient.receiveMessages(5)) {
                queueMessages.append(message.getBody().toString()+",");
                queueClient.updateMessage(message.getMessageId(),
                message.getPopReceipt(),
                message.getBody().toString(), Duration.ofSeconds(1));

                if(message.getDequeueCount() > 3){
                    log.info("Rotten message, dequeuing ..");
                }       
            
        }

        return "["+queueMessages.toString()+"]";
    }

    public void dequeueMessages(String queueName){
        try{

            QueueClient queueGeneratedClient = new QueueClientBuilder()
            .connectionString("DefaultEndpointsProtocol=https;" +
            "AccountName="+ azureQueueConfig.getAccountName() + ";" +
            "AccountKey=" + azureQueueConfig.getAccountKey())
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

    public void thumbnailUploadDataBase(Integer slotNumber, Integer id, String thumbnailImage) {
        Optional<Task> taskfind = taskRepository.findTaskById(id);
        String filePath = "https://emeaacc2022tjavastoacc.blob.core.windows.net/files/" + thumbnailImage;
        if (taskfind.isPresent()) {
            if ((slotNumber >= FileServiceEnum.FIRSTSLOT.getSlotNumber())
                    && (slotNumber <= FileServiceEnum.THIRDSLOT.getSlotNumber())) {
                Task task = taskfind.get();
                if (slotNumber == FileServiceEnum.FIRSTSLOT.getSlotNumber()) {
                    task.setThumbnailFile1(filePath);
                }
    
                if (slotNumber == FileServiceEnum.SECONDSLOT.getSlotNumber()) {
                    task.setThumbnailFile2(filePath);
                }
    
                if (slotNumber == FileServiceEnum.THIRDSLOT.getSlotNumber()) {
                    task.setThumbnailFile3(filePath);
                }

                taskRepository.save(task);
    
            } else {
    
                throw new InvalidParamException("Not valid slot");
    
            }
    
            } else {
    
                throw new InvalidParamException("task Not Present");
    
            }
    
        }

    public void thumbnailUpload(String message) throws IOException{
        JSONArray jsonArray = new JSONArray(message);  

        try{ 

            for(int i=0; i < jsonArray.length(); i++){  
                JSONObject jsonObject = jsonArray.getJSONObject(i);  

                log.info("Retrieving " + jsonArray.length() + " thumbnails ..");

                Integer queueImageTaskId = jsonObject.getInt("taskid");
                Integer queueImageSlot = jsonObject.getInt("slot");
                String queueImageName = jsonObject.getString("image");

                thumbnailUploadDataBase(queueImageSlot, queueImageTaskId, queueImageName );

                dequeueMessages("already-generated-queue");
                
                log.info("Process Finished");
                
            }

        }catch(Exception e){
            log.info("No messages at the moment ..");
        }
        log.info("Nothing to do ..");
    }


    @Scheduled (fixedDelay = 10000)
    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() throws IOException {

        //generateThumbnailAndUpload(receiveMessages());
        thumbnailUpload(receiveMessages());

    }
}
