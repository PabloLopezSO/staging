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
import com.example.demo.service.LoginService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="superUser")
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})
class TodoAppControllerGetRecordsByIdTest {

    @Autowired
	private MockMvc mockMvc;
	
	@Autowired
	TaskRepository todoAppRepository;

	@Autowired
	UserRepository userRepository;

    @Mock
    private TaskRepository mockTaskRepo;
	
	@BeforeEach
	void setUp() {
		todoAppRepository.deleteAll();
	}

	@MockBean
    private LoginService ls;
	
	@Test
	void recordsById() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		User user2 = User.builder().mail("diego.fernandez@softwareone.com").build();
		userRepository.save(user2);

		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(2).build();

		todoAppRepository.save(task1);
		todoAppRepository.save(task2);

        mockMvc.perform(get("/api/tasks/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
			.andExpect(jsonPath("$.creator").value("testmail@softwareone.com"));

        mockMvc.perform(get("/api/tasks/2"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("2"))
			.andExpect(jsonPath("$.creator").value("diego.fernandez@softwareone.com"));
		
	}

	@Test
	void recordsByIdNotFound() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task3);

        mockMvc.perform(get("/api/tasks/18"))
            .andDo(print())
            .andExpect(status().isNotFound());
		
	}

	@Test
	void recordsByIdValidate() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task4 = Task.builder().title("title 4").description("description 4").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task4);

        mockMvc.perform(get("/api/tasks/s"))
            .andDo(print())
            .andExpect(status().isBadRequest());
		
	}
    
}
