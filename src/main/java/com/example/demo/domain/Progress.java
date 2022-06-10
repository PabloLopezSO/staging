package com.example.demo.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="progress")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Progress {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	

	@NotNull
	private String newStatus;
	
	@NotNull
	private LocalDateTime modifiedDate;

    @NotNull
	@JsonInclude(Include.NON_NULL)
	private Integer taskId;
	
	@NotNull
	@JsonInclude(Include.NON_NULL)
	private Integer userID;
    
}
