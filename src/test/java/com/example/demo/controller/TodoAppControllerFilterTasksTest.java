package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
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

class TodoAppControllerFilterTasksTest{

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

    @Test
	void recordsByStatusCorrect() throws Exception {		

		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		
		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);
		todoAppRepository.save(task2);
		todoAppRepository.save(task3);

        mockMvc.perform(get("/api/tasks?status=1"))
            .andDo(print())
            .andExpect(jsonPath("$.tasks.length()").value(3))
			
			.andExpect(jsonPath("$.tasks[0].status").value("1"))

			.andExpect(jsonPath("$.tasks[1].status").value("1"))

			.andExpect(jsonPath("$.tasks[2].status").value("1"));
	}

	@Test
	void recordsByKeywordCorrect() throws Exception {	
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		
		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);
		todoAppRepository.save(task2);
		todoAppRepository.save(task3);

        mockMvc.perform(get("/api/tasks?keyword=titl"))
            .andDo(print())
            .andExpect(jsonPath("$.tasks.length()").value(3))
			
			.andExpect(jsonPath("$.tasks[0].title").value("title 1"))

			.andExpect(jsonPath("$.tasks[1].title").value("title 2"))

			.andExpect(jsonPath("$.tasks[2].title").value("title 3"));
	}

	@Test
	void recordsByKeywordAndStatusCorrect() throws Exception {		
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(2).creator(1).build();
		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);
		todoAppRepository.save(task2);
		todoAppRepository.save(task3);

        mockMvc.perform(get("/api/tasks?keyword=titl&status=1"))
            .andDo(print())
            .andExpect(jsonPath("$.tasks.length()").value(2))
			
			.andExpect(jsonPath("$.tasks[0].title").value("title 1"))

			.andExpect(jsonPath("$.tasks[1].title").value("title 3"));
	}

	@ParameterizedTest
	@MethodSource("endPointProvider")
	void testCounterIsCorrect(String endPoint) throws Exception{

		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task2 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		Task task3 = Task.builder().title("title 3").description("description 3").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);
 		todoAppRepository.save(task2);
		todoAppRepository.save(task3);

		mockMvc.perform( get("/api/tasks?"+endPoint))
		
			.andExpect(jsonPath("$.counter").value(3));
	
	}

	static Stream<String> endPointProvider(){
		return Stream.of("status=1","keyword=description","keyword=t&status=1");
	}

	@Test
	void recordsByStatusNotNumericError() throws Exception {

		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		
		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks?status=x"))
            .andDo(print())
            .andExpect(status().isBadRequest());
		
	}

    
	
}
