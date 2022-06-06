package com.example.demo.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;



@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="superUser")
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})

class TodoAppControllerGetAllRecordsByCreationDateTest {
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
	void taskListDateOrderedAsc() throws Exception {

		LocalDateTime date = LocalDateTime.of(2022, 4, 21, 10, 00, 27);
		LocalDateTime date1 = LocalDateTime.of(2022, 3, 27, 10, 00, 27);
		LocalDateTime date2 = LocalDateTime.of(2022, 1, 14, 10, 00, 27);

		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(date2).status(1).creator(1).build();
        Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(date).status(1).creator(1).build();
		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(LocalDateTime.now()).createdDate(date1).status(1).creator(1).build();

		todoAppRepository.save(task1);
 		todoAppRepository.save(task2);
		todoAppRepository.save(task3);


		
		mockMvc.perform( get("/api/tasks"))
		
			.andDo(print())
			.andExpect(status().isOk())

			.andExpect(jsonPath("$.tasks.length()").value(3))
			
			.andExpect(jsonPath("$.tasks[0].createdDate").value("2022-01-14 10:00:27"))

			.andExpect(jsonPath("$.tasks[1].createdDate").value("2022-03-27 10:00:27"))

			.andExpect(jsonPath("$.tasks[2].createdDate").value("2022-04-21 10:00:27"));
		
	}

	@Test
	void taskListCounterCorrect() throws Exception {

		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		
		LocalDateTime date = LocalDateTime.of(2022, 4, 21, 10, 00, 27);
		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(date).createdDate(date).status(1).creator(1).build();
        Task task2 = Task.builder().title("title 2").description("description 2").dueDate(date).createdDate(date).status(1).creator(1).build();
		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(date).createdDate(date).status(1).creator(1).build();

		todoAppRepository.save(task1);
 		todoAppRepository.save(task2);
		todoAppRepository.save(task3);
		
		mockMvc.perform( get("/api/tasks"))
		
			.andExpect(jsonPath("$.counter").value(3));
	
	}
}