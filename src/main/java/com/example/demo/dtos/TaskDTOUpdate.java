package com.example.demo.dtos;

import java.time.LocalDateTime;

import com.example.demo.domain.Task;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTOUpdate {
	
	private String title;
	
	private String description;

	private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dueDate;

	public static TaskDTOUpdate taskToTaskDTOUpdate(Task task){

		TaskDTOUpdate taskDTOUpdate = new TaskDTOUpdate();

		taskDTOUpdate.title = task.getTitle();
		taskDTOUpdate.status = task.getStatus();
		taskDTOUpdate.description = task.getDescription();
		taskDTOUpdate.dueDate = task.getDueDate();

		return taskDTOUpdate;
	}

}

