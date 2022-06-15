package com.example.demo.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="tasks")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Task {

	@Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(unique=true)
	@NotNull
	private String title;
	
	@JsonInclude(Include.NON_NULL)
	private String description;

	@NotNull
	private Integer status;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dueDate;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	@NotNull
	@JsonInclude(Include.NON_NULL)
	private Integer creator;

	private Integer assignee;

	private String file1;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime file1UploadDate;

	private String azureFile1;

	private String downloadFile1;

	private String thumbnailFile1;

	private String file2;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime file2UploadDate;

	private String azureFile2;

	private String downloadFile2;

	private String thumbnailFile2;

	private String file3;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime file3UploadDate;

	private String azureFile3;

	private String downloadFile3;

	private String thumbnailFile3;
	
	public Task(Integer id, String title, Integer status, LocalDateTime dueDate, LocalDateTime createdDate){

		this.id = id;
		this.title = title;
		this.status = status;
		this.dueDate = dueDate;
		this.createdDate = createdDate;

	}
		
}	
	
