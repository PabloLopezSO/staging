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
public class TaskDTODetail {

    private Integer id;
    
	private String title;
	
	private String description;

	private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	private String creator;

	private String Assignee;

	private String file1;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime file1UploadDate;

	private String thumbnailFile1;

	private String file2;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime file2UploadDate;

	private String thumbnailFile2;

	private String file3;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime file3UploadDate;

	private String thumbnailFile3;

	public TaskDTODetail(Task task){

		this.id = task.getId();
		this.title = task.getTitle();
		this.status = task.getStatus();
		this.dueDate = task.getDueDate();
		this.createdDate = task.getCreatedDate();
		this.file1=task.getFile1();
		this.file2=task.getFile2();
		this.file3=task.getFile3();
		this.file1UploadDate=task.getFile1UploadDate();
		this.file2UploadDate=task.getFile2UploadDate();
		this.file3UploadDate=task.getFile3UploadDate();

	}
    
}
