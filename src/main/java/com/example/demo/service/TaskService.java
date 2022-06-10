package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import com.example.demo.domain.TaskStatus;
import com.example.demo.dtos.PaginationDTO;
import com.example.demo.dtos.TaskDTODetail;
import com.example.demo.dtos.TaskDTOUpdate;
import com.example.demo.exception.InvalidParamException;
import com.example.demo.exception.NoSuchIdException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.decorator.PageDecorator;
import com.example.demo.domain.Task;
import com.example.demo.domain.Progress;
import com.example.demo.repository.ProgressRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

@Service
public class TaskService {
	
	private TaskRepository taskRepository;
	private PaginationDTO paginationDTO = new PaginationDTO();
	private ProgressService progressService;
	private ProgressRepository progressRepository;
	private FileService fileService;
	private UserRepository userRepository;
	
	
	@Autowired
	public TaskService(TaskRepository todoAppRepository, ProgressService progressService, ProgressRepository progressRepository, UserRepository userRepository, FileService fileService) {
		this.taskRepository = todoAppRepository;
		this.progressService = progressService;
		this.progressRepository = progressRepository;
		this.fileService = fileService;
		this.userRepository = userRepository;
	}

	public Task insert(Task task) {

		return taskRepository.save(task);
	}

	@Transactional
	public Task updateATask(Integer id, TaskDTOUpdate taskdto, Integer userId){		

		Optional<Task> taskOpt=taskRepository.findById(id);

		if(taskOpt.isPresent()){	
			Task task = taskOpt.get();	
			Integer originalStauts = task.getStatus();

			if(Boolean.TRUE.equals(isValidTitle(taskdto.getTitle()))){
				task.setTitle(taskdto.getTitle());
			}
			if(Boolean.TRUE.equals(isValidDescription(taskdto.getDescription()))){
				task.setDescription(taskdto.getDescription());
			}
			if(Boolean.TRUE.equals(isValidStatus(taskdto.getStatus()))){
				task.setStatus(taskdto.getStatus());
			}
			if(Boolean.TRUE.equals(isValidDueDate(taskdto.getDueDate()))){
				task.setDueDate(taskdto.getDueDate());
			}	

			taskRepository.save(task);

			//Analyze if the status has been modified and store the change in its case
			if(!originalStauts.equals(task.getStatus())){
				TaskStatus status = TaskStatus.from(task.getStatus()) ;
				Progress progressUpdate = Progress.builder().newStatus(status.toString()).modifiedDate(LocalDateTime.now()).taskId(task.getId()).userID(userId).build();
				progressService.insert(progressUpdate);
			}

			return task;

		}else{
			throw new NoSuchIdException(String.format("The task with ID '%d' does not exist ",id));
		}

	}

	private Boolean isValidStatus(Integer status){

		Boolean statusValid=false;

		if(status!=null){

			if(!TaskStatus.INVALID.equals(TaskStatus.from(status))){
				
				statusValid=true;

			}else{
				throw new InvalidParamException(String.format("The status '%d' is not valid",status));
			}	
		}

        return statusValid;
    }

	private Boolean isValidDueDate(LocalDateTime dueDate){

		Boolean dueDateValid=false;

		if(dueDate!=null){

			if(Boolean.TRUE.equals(!dueDate.isBefore(LocalDateTime.now()))){

				dueDateValid=true;

			}
			else{
				throw new InvalidParamException(String.format("The date '%s' must be after current date",dueDate));
			}
		}

		return dueDateValid;
	}

	private Boolean isValidTitle(String title){
		
		Boolean validTitle=false;
		
		if(title!=null){

			if(!taskRepository.findByTitle(title).isPresent()){

				validTitle=true;

			}else{
				throw new InvalidParamException(String.format("The title '%s' is not unique",title));
			}
		}

		return validTitle;
	}

	public Boolean isValidDescription(String description){
		return description != null;
	}

	
	public PageDecorator<TaskDTODetail> allRecords(Optional<Integer> status, Optional<String> keyword, Optional<Integer> size, Optional<Integer> customSize, int pageNumber ){
	
		//"User asked for order records"
		
		paginationDTO.setPageNumber(pageNumber);
		paginationDTO.setPageSize(size);
		
		Page<Task> page;
		Pageable pageable = PageRequest.of(paginationDTO.getPageNumber(), paginationDTO.getPageSize());
		Pageable allItems = Pageable.unpaged();

		//In case the keyword is not present we set an empty string 
		String keywordSTR = keyword.isPresent() ? keyword.get().toLowerCase() : "";

			if(status.isPresent() && !TaskStatus.INVALID.equals(TaskStatus.from(status.get()))){
				//"User asked for List of tasks filtered by status and keyword"
				page = taskRepository.findByKeywordAndStatus(keywordSTR, status.get(), pageable);
				return new PageDecorator<>(page.map(TaskDTODetail::new), pageable.getPageNumber());
			}

			if(status.isPresent()  && TaskStatus.INVALID.equals(TaskStatus.from(status.get()))){

				throw new InvalidParamException(String.format("The status '%s' is not valid",status));
			}
	
			if(!keywordSTR.isEmpty()){
	
				page = taskRepository.findByKeyword(keywordSTR, pageable);
				return new PageDecorator<>(page.map(TaskDTODetail::new), pageable.getPageNumber());
			}

			if(customSize.isPresent()){
				paginationDTO.setPageSize(size);
				page = taskRepository.findAllOrderByCreatedDateAsc(pageable);
				
				return new PageDecorator<>(page.map(TaskDTODetail::new), pageable.getPageNumber());
			}


		page = taskRepository.findAllOrderByCreatedDateAsc(allItems);
				
		return new PageDecorator<>(page.map(TaskDTODetail::new), pageable.getPageNumber());
						
	}

	@Transactional
	public void deleteTask(Integer taskId){

		Optional<Task> foundTaskById = taskRepository.findById(taskId);

		if (foundTaskById.isPresent()) {

			Task taskToDelete = foundTaskById.get();

			if (TaskStatus.from(taskToDelete.getStatus()).equals(TaskStatus.DONE)) {
					
				List<Progress> progressList = progressRepository.findAllByTaskId(taskId);
					
        		for (Progress progress : progressList) {

    			progressRepository.delete(progress);
        		}
				fileService.deleteFiles(taskId);
				taskRepository.delete(taskToDelete);

			} else {

			//Invalid delete operation: only Done status tasks can be removed"
			throw new InvalidParamException("The status  must be done");

			}

		} else {
			//Error: the indicated task Id couldn't be found"
			
			throw new NoSuchIdException("Task not found");
		}
	
	}
	
	public boolean validateCreatorOrAssignee(Integer taskid, String creator){

		Optional<Task> taskFind=taskRepository.findTaskById(taskid);
		
		if(taskFind.isPresent()){
			Task task = taskFind.get();

			String userCreator = userRepository.findNameById(task.getCreator());
			String userAssignee = userRepository.findNameById(task.getAssignee());

			if((creator.equals(userCreator))||(creator.equals(userAssignee))){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			throw new NoSuchIdException("Task no encontrada");
		}
	}
}

