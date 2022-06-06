package com.example.demo.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;


import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;



@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="superUser")
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})

public class TodoAppControllerDeleteTaskTest {
    @Autowired
	private MockMvc mockMvc;
	
	@Autowired
	TaskRepository todoAppRepository;

	@Autowired
	UserRepository userRepository;
	
	@BeforeEach
	void setUp() {
		todoAppRepository.deleteAll();
	}

    @Test
	void recordDeletedFailedTaskDontExists() throws Exception {

        mockMvc.perform(delete("/api/tasks/1"))
            .andDo(print())
            .andExpect(status().isNotFound());
		
	}

    @Test
	void recordDeletedFailedTypeMismatch() throws Exception {

        mockMvc.perform(delete("/api/tasks/s"))
            .andDo(print())
            .andExpect(status().isBadRequest());
		
	}

    @Test
	void recordDeletedFailedIncorrectStatus() throws Exception {

		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		Task task = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task);

        mockMvc.perform(delete("/api/tasks/1"))
            .andDo(print())
            .andExpect(status().isBadRequest());
		
	}

	@Test
	void recordDeletedCorrect() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();

		todoAppRepository.save(task2);

        mockMvc.perform(delete("/api/tasks/1"))
            .andDo(print())
            .andExpect(status().isOk());
		
	}
}
