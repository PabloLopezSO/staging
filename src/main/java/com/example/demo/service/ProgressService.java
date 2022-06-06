package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.domain.Progress;
import com.example.demo.domain.Task;
import com.example.demo.dtos.ProgressDTO;
import com.example.demo.dtos.ProgressListDTO;
import com.example.demo.repository.ProgressRepository;
import com.example.demo.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
public class ProgressService {

    private ProgressRepository progressRepository;
    private TaskRepository taskRepository;
    private UserService userService;

    @Autowired
	public ProgressService(ProgressRepository progressRepository, TaskRepository taskRepository, UserService userService) {
		this.progressRepository = progressRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
	}

	public void insert(Progress progress) {

		progressRepository.save(progress);
	}

    public ProgressListDTO taskProgress(Integer taskId){

        //Analyze if the task exists

        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if(taskOpt.isPresent()){

            String userName = userService.findCreator(taskOpt.get().getCreator());

            List<Progress> lProgress = progressRepository.findAllByTaskId(taskId);

            List<ProgressDTO> listProvisional = new ArrayList<>();

            //Change the id's by the title and mail refered to the task and creator

            for (Progress progress : lProgress) {

                ProgressDTO progressDTO = ProgressDTO.builder().id(progress.getId())
                .modifiedDate(progress.getModifiedDate())
                .newStatus(progress.getNewStatus())
                .task(taskOpt.get().getTitle())
                .user(userName)
                .build();

                listProvisional.add(progressDTO);
            }
            //Send the class ProgressListDTO with the special designed fields
            return ProgressListDTO.builder().progresses(listProvisional).build();

           
        }else{

            throw new NotFoundException("Task id does not exist");
        }

        

    }
    
}
