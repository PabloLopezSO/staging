package com.example.demo.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.decorator.PageDecorator;
import com.example.demo.domain.Progress;
import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.domain.TaskStatus;
import com.example.demo.dtos.TaskDTOUpdate;
import com.example.demo.dtos.UserCredentialsDTO;
import com.example.demo.exception.InvalidParamException;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dtos.ProgressListDTO;
import com.example.demo.dtos.TaskDTODetail;
import com.example.demo.service.FileService;
import com.example.demo.service.LoginService;
import com.example.demo.service.ProgressService;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.log4j.Log4j2;



@RequestMapping("/api")
@RestController
@Log4j2

public class TodoAppController {

	private TaskService taskService;
	private TaskRepository taskRepository;
	private LoginService loginService;
	private UserService userService;
	private final FileService fileService;
	private ProgressService progressService;


	@Autowired
	public TodoAppController(TaskService todoAppService, TaskRepository taskRepository, LoginService loginService,  UserService userService, FileService fileService , ProgressService progressService) {
		this.taskService = todoAppService;
		this.taskRepository = taskRepository;
		this.loginService = loginService;
		this.userService = userService;
		this.fileService = fileService;
		this.progressService = progressService;
	}

	@GetMapping("/tasks")
	public ResponseEntity<PageDecorator<TaskDTODetail>> getAllRecordsByCreationDate(@RequestParam Optional<Integer> status, @RequestParam Optional<String> keyword, 
	@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") Optional<Integer> size, @RequestParam(name = "size") Optional<Integer> customSize) {

		return new ResponseEntity<>(taskService.allRecords(status, keyword, size, customSize, page), HttpStatus.OK);
	}
	
	@GetMapping("/tasks/{taskid}")
	public  ResponseEntity<TaskDTODetail>getTaskById(@PathVariable String taskid,  HttpServletResponse response) throws IOException {

		try {

			Integer taskidValidate = Integer.parseInt(taskid);

			log.info("User asked for task by Id: "+taskidValidate);

			Optional<Task> foundTaskById = taskRepository.findById(taskidValidate);

			if (foundTaskById.isPresent()) {

				TaskDTODetail taskToShow = new TaskDTODetail();

				taskToShow.setId( foundTaskById.get().getId());
				taskToShow.setTitle(foundTaskById.get().getTitle());
				taskToShow.setDescription(foundTaskById.get().getDescription());
				taskToShow.setCreatedDate(foundTaskById.get().getCreatedDate());
				taskToShow.setDueDate(foundTaskById.get().getDueDate());
				taskToShow.setStatus(foundTaskById.get().getStatus());
				taskToShow.setFile1(foundTaskById.get().getFile1());
				taskToShow.setFile2(foundTaskById.get().getFile2());
				taskToShow.setFile3(foundTaskById.get().getFile3());
				taskToShow.setFile1UploadDate(foundTaskById.get().getFile1UploadDate());
				taskToShow.setFile2UploadDate(foundTaskById.get().getFile2UploadDate());
				taskToShow.setFile3UploadDate(foundTaskById.get().getFile3UploadDate());
				taskToShow.setThumbnailFile1(foundTaskById.get().getThumbnailFile1());
				taskToShow.setThumbnailFile2(foundTaskById.get().getThumbnailFile2());
				taskToShow.setThumbnailFile3(foundTaskById.get().getThumbnailFile3());
				

				log.info("Looking for the user with Id:  "+foundTaskById.get().getCreator());
				taskToShow.setCreator(userService.findCreator(foundTaskById.get().getCreator()));
				taskToShow.setAssignee(userService.findCreator(foundTaskById.get().getAssignee()));
				return new ResponseEntity<>(taskToShow, HttpStatus.OK);

			} else {

				log.info("Task not found");
				
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

		} catch (NumberFormatException e) {

			log.info("Type error", e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		} catch (Exception e) {

			log.info("Internal Server Error" + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/tasks")
	public ResponseEntity<Task> addTask( @RequestBody Task task) {

		

			String creator = SecurityContextHolder.getContext().getAuthentication().getName();

			log.info("task {} request to be inserted", task.getTitle());

			try {

				log.info("Looking for a repeated task with the same title as given");
				if (taskRepository.findByTitle(task.getTitle()).isPresent()) {

					log.info("The title is repeated so the 409 http status code");
					return new ResponseEntity<>(HttpStatus.CONFLICT);
				}

				log.info("Reviewing that every field is provided by the client");
				if (task.getTitle().isEmpty() ||
					task.getDescription().isEmpty() ||
					task.getDueDate() == null) {

					log.info("Some mandatory field is not provided so it is shown the 400 http status code ");
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}

				log.info("Checking if DueDateDate date is lesser than current date");
				if (task.getDueDate().isBefore(LocalDateTime.now())) {

					log.info("DueDateDate date is surpased, so it is shown the 400 http status code ");
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}

				Integer idUser = userService.checkUserAndSave(creator);
							

				log.info("Set the information not provided by the client as given on the issue");
				task.setStatus(TaskStatus.TO_DO.getStatusNumber());
				task.setCreatedDate(LocalDateTime.now());
				task.setCreator(idUser);

				

				Task taskToInsert = taskService.insert(task);
				Progress progressCreate = Progress.builder().newStatus("CREATED").modifiedDate(LocalDateTime.now()).taskId(taskToInsert.getId()).userID(idUser).build();
				this.progressService.insert(progressCreate);

				log.info("task {} inserted with id '{}' shown http status code 201", task.getTitle(), task.getId());

				return new ResponseEntity<>(taskToInsert, HttpStatus.CREATED);

			} catch (Exception e) {

				log.info("{} error inserting '{}' task", e.getMessage(), task.getTitle());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

		

	}

	@DeleteMapping("/tasks/{taskid}")
	public ResponseEntity<Void> deleteTaskById(@PathVariable Integer taskid) {

		taskService.deleteTask(taskid);
		return new ResponseEntity<>(HttpStatus.OK);
			
    }
	
	@PatchMapping(value = {"/tasks/{taskid}","/v2/tasks/{taskid}"})
	public TaskDTOUpdate patchATaskById(@PathVariable Integer taskid, @RequestBody TaskDTOUpdate taskdto){

		String creator = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(taskService.validateCreatorOrAssignee(taskid, creator)){

			log.debug("Analyze if the user is in the database");
			Integer idUser = userService.checkUserAndSave(creator);

			log.debug("Update the task and the status progress");
			return TaskDTOUpdate.taskToTaskDTOUpdate(taskService.updateATask(taskid, taskdto, idUser));
		
		}else {
			throw new InvalidParamException("The user has not permission");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> doLogin(@RequestBody UserCredentialsDTO userCredentials) throws JsonProcessingException {

		return new ResponseEntity<>(loginService.loginUserByToken(userCredentials), HttpStatus.OK);
	}

	@PatchMapping(value = "/tasks/{taskid}/files/{slotNumber}") 
	public Task upload(@PathVariable Integer taskid,@RequestParam("file") MultipartFile file,@PathVariable Integer slotNumber) {
		return fileService.uploadFile(taskid, file, slotNumber);
	}

	@DeleteMapping(value = "/tasks/{taskid}/files")
	public void deleteFile(@PathVariable Integer taskid, @RequestParam String filename) {
        fileService.filePresent(filename,taskid);
	}

	@DeleteMapping(value = "/tasks/{taskid}/files/{slotNumber}")
	public void deleteFileBySlot(@PathVariable Integer taskid, @PathVariable Integer slotNumber) {
        fileService.fileDeleteSlot(slotNumber,taskid);
	}

	@GetMapping("/tasks/{taskid}/progresses")
	public ResponseEntity<ProgressListDTO> taskProgress(@PathVariable Integer taskid){

		ProgressListDTO progressListDTO = progressService.taskProgress(taskid);
		return new ResponseEntity<>(progressListDTO, HttpStatus.OK);

	} 

	@GetMapping("/tasks/{taskId}/files/{slot}")
	public ResponseEntity<Object> download( @PathVariable Integer taskId, @PathVariable Integer slot) throws URISyntaxException{

    	return new ResponseEntity<>(fileService.downloadFile(taskId, slot), HttpStatus.SEE_OTHER);

	}

	@PatchMapping("/tasks/{taskid}/assignee/{username}")
	public ResponseEntity<String> updateAssignee( @PathVariable Integer taskid, @PathVariable String username){
		
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		
		if(taskService.validateCreatorOrAssignee(taskid, currentUser)){

			if(taskService.modifyAssignee(taskid, username)){

				return new ResponseEntity<>(String.format("The user %s has been set as Assignee on task %d", username, taskid), HttpStatus.OK);
			}
			else{

				throw new InvalidParamException("User not found");

			}
		
		}else {
			throw new InvalidParamException("The user has not permission");
		}
	}
}
