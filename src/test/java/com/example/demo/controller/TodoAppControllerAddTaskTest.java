package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.repository.TaskRepository;
import com.example.demo.service.LoginService;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({ "/drop.sql", "/create.sql", "/testDataOnlyTaskStatuses.sql" })
class TodoAppControllerAddTaskTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	TaskRepository todoAppRepository;

	@BeforeEach
	void setUp() {
		todoAppRepository.deleteAll();

	}

	@MockBean
    private LoginService ls;



	@Test
    void insertTaskCorrect() throws Exception {

        String taskToInsert = "{\r\n    \"title\": \"FirstNote\",\r\n    \"description\": \"cool thing to do\", \"dueDate\":\"2023-02-02 10:10:00\" }";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime testingDate = LocalDateTime.now();

        String resultado = testingDate.format(dtf);
		
		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");		
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.id").isNotEmpty())

                .andExpect(jsonPath("$.title").value("FirstNote"))

                .andExpect(jsonPath("$.status").value(1))

                .andExpect(jsonPath("$.description").value("cool thing to do"))

                .andExpect(jsonPath("$.createdDate").value(resultado.toString()))

				.andExpect(jsonPath("$.creator").value(1));


    }

	@Test
	void addTaskInsertAndRetrieve() throws Exception {

		String taskToInsert = "{\r\n    \"title\": \"FirstNote\",\r\n    \"description\": \"cool thing to do\", \"dueDate\":\"2023-02-02 10:00:27\" }";
		String taskToInsert2 = "{\r\n    \"title\": \"SecondNote\",\r\n    \"description\": \"2 cool things to do\", \"dueDate\":\"2023-02-02 10:00:27\"  }";

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");		
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert))

				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.title").value("FirstNote"))
				.andExpect(jsonPath("$.description").value("cool thing to do"));

		mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert2))

				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.title").value("SecondNote"))
				.andExpect(jsonPath("$.description").value("2 cool things to do"));

	}

	@Test
	void addTaskDoubledTitleError() throws Exception {

		String taskToInsert = "{\r\n    \"title\": \"FirstNote\",\r\n    \"description\": \"cool thing to do\", \"dueDate\":\"2023-02-02 10:00:27\" }";
		String taskToInsert2 = "{\r\n    \"title\": \"FirstNote\",\r\n    \"description\": \"2 cool things to do\", \"dueDate\":\"2023-02-02 10:00:27\"  }";

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert))

				.andExpect(status().isCreated());

		mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert2))

				.andExpect(status().isConflict());

	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{\r\n \"title\": \"\",\r\n \"description\": \"cool thing to do\",	\"dueDate\":\"2023-02-02 10:00:27\" }",
			"{\r\n \"title\": \"titulo1\",\r\n \"description\": \"\",	\"dueDate\":\"2023-02-02 10:00:27\" }",
			"{\r\n \"title\": \"titulo1\",\r\n \"description\": \"cool thing to do\",	\"dueDate\":\"\" }"
	})
	void addTaskEmptyFieldError(String taskToInsert) throws Exception {

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert))

				.andExpect(status().isBadRequest());

	}

	@Test
	void addTaskDueDateEarlierThanTodayError() throws Exception {

		String taskToInsert = "{\r\n    \"title\": \"titulo1\",\r\n    \"description\": \"cool thing to do\", \"dueDate\":\"2021-02-02 10:00:27\" }";

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert))

				.andExpect(status().isBadRequest());

	}

}
