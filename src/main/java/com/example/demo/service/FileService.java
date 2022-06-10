package com.example.demo.service;

import java.util.Optional;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlockBlobItem;
import com.azure.storage.blob.specialized.BlockBlobClient;
import com.example.demo.domain.FileServiceEnum;
import com.example.demo.domain.Task;
import com.example.demo.exception.InvalidParamException;

import com.example.demo.exception.NoSuchIdException;
import com.example.demo.property.FileStorageProperties;
import com.example.demo.repository.TaskRepository;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Log4j2
@Service
public class FileService {
    private final Path fileStorageLocation;
    private final BlobServiceClient blobServiceClient;
    private TaskRepository taskRepository;
    private String containerClient="files";

    @Autowired
    public FileService(TaskRepository todoAppRepository,@NonNull FileStorageProperties fileStorageProperties, BlobServiceClient blobServiceClient) {                                                                                                                              
        // get the path of the upload directory
        fileStorageLocation = Path.of(fileStorageProperties.getUploadDir());
        this.blobServiceClient = blobServiceClient;
        this.taskRepository = todoAppRepository;
        try {
            // creates directory/directories, if directory already exists, it will not throw exception
            Files.createDirectories(fileStorageLocation);
        } catch (IOException e) {
            log.error("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    public Task uploadFile(Integer id,@NonNull MultipartFile file, Integer fileNumber){                                                      
        BlobContainerClient blobContainerClient = getBlobContainerClient(containerClient);
        String filename = file.getOriginalFilename();
        String filenameURL = id+"_"+file.getOriginalFilename();
        BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(filenameURL).getBlockBlobClient();
        Optional<Task> taskfind=taskRepository.findTaskById(id);
        Task task = taskfind.get();
        try {
            // upload file to azure blob storage
            if((fileNumber>=FileServiceEnum.FIRSTSLOT.getSlotNumber())&&(fileNumber<=FileServiceEnum.THIRDSLOT.getSlotNumber())){
                fileFilter(file);  
                filePresent(filename, id);
                BlockBlobItem blobItem = blockBlobClient.upload(new BufferedInputStream(file.getInputStream()), file.getSize(), true );
                String tempFilePath = fileStorageLocation + "/" + filenameURL;
                Files.deleteIfExists(Paths.get(tempFilePath));
                String filePath="https://emeaacc2022tjavastoacc.blob.core.windows.net/files/" + filenameURL;
                if(fileNumber==FileServiceEnum.FIRSTSLOT.getSlotNumber()){
                    task.setFile1(filename);
                    task.setFile1UploadDate(LocalDateTime.now());
                    task.setAzureFile1(filenameURL);
                    task.setDownloadFile1(filePath);
                }
                if(fileNumber==FileServiceEnum.SECONDSLOT.getSlotNumber()){
                    task.setFile2(filename);
                    task.setFile2UploadDate(LocalDateTime.now());
                    task.setAzureFile2(filenameURL);
                    task.setDownloadFile2(filePath);
                }
                if(fileNumber==FileServiceEnum.THIRDSLOT.getSlotNumber()){
                    task.setFile3(filename);
                    task.setFile3UploadDate(LocalDateTime.now());
                    task.setAzureFile3(filenameURL);
                    task.setDownloadFile3(filePath);
                }
                taskRepository.save(task);
            }
            else{
                throw new InvalidParamException("The Filenumber is not valid");
            }    
        } catch (IOException e) {
            log.error("Error while processing file {}", e.getLocalizedMessage());
        }
        return task;
    }


    private void fileFilter(MultipartFile file){
        if (file == null || file.getContentType() == null || file.isEmpty() || file.getSize() == 0) {
            throw new InvalidParamException(("The file is not valid"));
        } else {
        if (!("image/jpg".equalsIgnoreCase(file.getContentType())
        || "application/pdf".equalsIgnoreCase(file.getContentType())
        || "text/plain".equalsIgnoreCase(file.getContentType()))) {
                throw new InvalidParamException("The file is not valid");
            }

        }
    }

    public void filePresent(String filename, Integer id){

        Optional<Task> taskfind=taskRepository.findTaskById(id);
        if(taskfind.isPresent()){
            Task task = taskfind.get();
            BlobContainerClient blobContainerClient = getBlobContainerClient(containerClient);
            BlockBlobClient blockBlobClient1 = blobContainerClient.getBlobClient(task.getAzureFile1()).getBlockBlobClient();
            BlockBlobClient blockBlobClient2 = blobContainerClient.getBlobClient(task.getAzureFile2()).getBlockBlobClient();
            BlockBlobClient blockBlobClient3 = blobContainerClient.getBlobClient(task.getAzureFile3()).getBlockBlobClient();
            if((task.getAzureFile1()!=null)&&(task.getFile1().equals(filename))){
                task.setFile1(null);
                delete(blockBlobClient1);
                task.setFile1UploadDate(null);
                task.setAzureFile1(null);
                task.setDownloadFile1(null);
            }
            if((task.getAzureFile2()!=null)&&(task.getFile2().equals(filename))){
                task.setFile2(null);
                delete(blockBlobClient2);
                task.setFile2UploadDate(null);
                task.setAzureFile2(null);
                task.setDownloadFile2(null);
            }
            if((task.getAzureFile3()!=null)&&(task.getFile3().equals(filename))){
                task.setFile3(null);
                delete(blockBlobClient3);
                task.setFile3UploadDate(null);
                task.setAzureFile3(null);
                task.setDownloadFile3(null);
            }
            taskRepository.save(task);
        }
        else{
            throw new  InvalidParamException("task not present");
        }   
    }

    public void fileDeleteSlot(Integer slotNumber, Integer id){

        Optional<Task> taskfind=taskRepository.findTaskById(id);
        if(taskfind.isPresent()){
            if((slotNumber>=FileServiceEnum.FIRSTSLOT.getSlotNumber())&&(slotNumber<=FileServiceEnum.THIRDSLOT.getSlotNumber())){
                Task task = taskfind.get();
                BlobContainerClient blobContainerClient = getBlobContainerClient(containerClient);
                BlockBlobClient blockBlobClient1 = blobContainerClient.getBlobClient(task.getAzureFile1()).getBlockBlobClient();
                BlockBlobClient blockBlobClient2 = blobContainerClient.getBlobClient(task.getAzureFile2()).getBlockBlobClient();
                BlockBlobClient blockBlobClient3 = blobContainerClient.getBlobClient(task.getAzureFile3()).getBlockBlobClient();
                if(slotNumber==FileServiceEnum.FIRSTSLOT.getSlotNumber()){
                    task.setFile1(null);
                    delete(blockBlobClient1);
                    task.setFile1UploadDate(null);
                    task.setAzureFile1(null);
                    task.setDownloadFile1(null);   
                }
                if(slotNumber==FileServiceEnum.SECONDSLOT.getSlotNumber()){
                    task.setFile2(null);
                    delete(blockBlobClient2);
                    task.setFile2UploadDate(null);
                    task.setAzureFile2(null);
                    task.setDownloadFile2(null);
                }
                if(slotNumber==FileServiceEnum.THIRDSLOT.getSlotNumber()){
                    task.setFile3(null);
                    delete(blockBlobClient3);
                    task.setFile3UploadDate(null);
                    task.setAzureFile3(null);
                    task.setDownloadFile3(null);
                }

                taskRepository.save(task);
            }
            else{
                throw new  InvalidParamException("Not valid slot");
            }
        }
        else{
            throw new  InvalidParamException("task not present");
        }   
    }
    
    public void delete(BlockBlobClient blockBlobClient){
        if (blockBlobClient.exists()) {
            blockBlobClient.delete();
        }
    }
    
    private @NonNull BlobContainerClient getBlobContainerClient(@NonNull String containerName) {                
        // create container if not exists
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
        return blobContainerClient;
    }

    public HttpHeaders downloadFile(Integer taskId, Integer slot) throws URISyntaxException{

        Optional<Task> taskOpt = taskRepository.findById(taskId);
        HttpHeaders httpHeaders = new HttpHeaders();
    
        //Analyze if the task exists
        if(taskOpt.isPresent()){
            Task task = taskOpt.get();

            //Analyze if the slot exists and has content
            return getSlotAndContent(httpHeaders, task, slot);

        }else{
            throw new NoSuchIdException("There is no task with that id");
        }
       
    }

    private HttpHeaders getSlotAndContent(HttpHeaders httpHeaders, Task task, Integer slot) throws URISyntaxException {
        URI uri;
        if(slot == FileServiceEnum.FIRSTSLOT.getSlotNumber()){
                
            if(task.getDownloadFile1()!=null){
                uri = new URI(task.getDownloadFile1());
                httpHeaders.setLocation(uri);
                return httpHeaders;
            }else{
                throw new NoSuchIdException("Empty slot");
            }
                

        }else if(slot == FileServiceEnum.SECONDSLOT.getSlotNumber()){
          
            if(task.getDownloadFile2()!=null){

                uri = new URI(task.getDownloadFile2());
                httpHeaders.setLocation(uri);
                return httpHeaders;
            
            }else{
               throw new NoSuchIdException("Empty slot");
            }
            
        }else if(slot == FileServiceEnum.THIRDSLOT.getSlotNumber()){
            
            if(task.getDownloadFile3()!=null){
                uri = new URI(task.getDownloadFile3());
                httpHeaders.setLocation(uri);
                return httpHeaders;
            
            }else{
                throw new NoSuchIdException("Empty slot");
            }
        }else{
            throw new  InvalidParamException("Slot does not exist");
        }    
        
    }     

    public void deleteFiles(Integer id){
        Optional<Task> taskfind=taskRepository.findTaskById(id);
        if(taskfind.isPresent()){
            Task task = taskfind.get();
            BlobContainerClient blobContainerClient = getBlobContainerClient(containerClient);
            if(task.getAzureFile1()!=null){
                BlockBlobClient blockBlobClient1 = blobContainerClient.getBlobClient(task.getAzureFile1()).getBlockBlobClient();
                task.setFile1(null);
                delete(blockBlobClient1);
                task.setFile1UploadDate(null);
                task.setAzureFile1(null);
                task.setDownloadFile1(null);
            }
            if(task.getAzureFile2()!=null){
                BlockBlobClient blockBlobClient2 = blobContainerClient.getBlobClient(task.getAzureFile2()).getBlockBlobClient();
                task.setFile2(null);
                delete(blockBlobClient2);
                task.setFile2UploadDate(null);
                task.setAzureFile2(null);
                task.setDownloadFile2(null);
            }
            if(task.getAzureFile3()!=null){
                BlockBlobClient blockBlobClient3 = blobContainerClient.getBlobClient(task.getAzureFile3()).getBlockBlobClient();
                task.setFile3(null);
                delete(blockBlobClient3);
                task.setFile3UploadDate(null);
                task.setAzureFile3(null);
                task.setDownloadFile3(null);
            }
        }
        else{
            throw new  InvalidParamException("task not present");
        }    
    }
}
