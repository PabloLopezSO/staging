package com.example.demo.controller;

import java.time.LocalDateTime;
import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoginService;
import com.example.demo.service.TaskService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="superUser")
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})
class TodoAppControllerStatusProgressTest {

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
        userRepository.deleteAll();
	}

	@MockBean
    private LoginService ls;

    @Mock
	private TaskService ts;

    @Test
    void statusCreated() throws Exception{

        String taskToInsert = "{\r\n    \"title\": \"title 1\",\r\n    \"description\": \"Testing description\", \"dueDate\":\"2023-02-02 10:10:00\" }";
      
		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
        Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);	
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert));


        mockMvc.perform(get("/api/tasks/1/progresses"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.progresses.length()").value(1))
            .andExpect(jsonPath("$.progresses[0].newStatus").value("CREATED"))
            .andExpect(jsonPath("$.progresses[0].task").value("title 1"))
			.andExpect(jsonPath("$.progresses[0].user").value("testmail@softwareone.com"));

    }

    @Test
    void statusUpdated() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
        Task task = Task.builder().title("title").description("description").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task);

        Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
        Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);

        String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        String taskToInsert = "{\r\n    \"title\": \"title 1\",\r\n    \"description\": \"Testing description\", \"dueDate\":\"2023-02-02 10:10:00\" }";
        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert));

        String taskUpdate = "{\"title\": \"title updated\", \"description\": \"description updated\", \"status\": 3, \"dueDate\":\"2023-02-02 10:00:27\" }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").header("Authorization", token).contentType("application/json").content(taskUpdate));

        mockMvc.perform(get("/api/tasks/1/progresses"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.progresses.length()").value(1))
            .andExpect(jsonPath("$.progresses[0].newStatus").value("DONE"))
            .andExpect(jsonPath("$.progresses[0].task").value("title updated"))
			.andExpect(jsonPath("$.progresses[0].user").value("testmail@softwareone.com"));

    }

    @Test
    void statusMultipleUpdates() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
        Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
        Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);	
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        String taskToInsert = "{\r\n    \"title\": \"title\",\r\n    \"description\": \"Testing description\", \"dueDate\":\"2023-02-02 10:10:00\" }";
        mockMvc.perform(post("/api/tasks").header("Authorization", token).contentType("application/json").content(taskToInsert));

        String taskUpdate = "{ \"status\": 3 }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").header("Authorization", token).contentType("application/json").content(taskUpdate));

        taskUpdate = "{ \"status\": 2 }";
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").header("Authorization", token).contentType("application/json").content(taskUpdate));

        mockMvc.perform(get("/api/tasks/1/progresses"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.progresses.length()").value(3))
            .andExpect(jsonPath("$.progresses[0].newStatus").value("CREATED"))
            .andExpect(jsonPath("$.progresses[0].task").value("title"))
			.andExpect(jsonPath("$.progresses[0].user").value("testmail@softwareone.com"))
            .andExpect(jsonPath("$.progresses[1].newStatus").value("DONE"))
            .andExpect(jsonPath("$.progresses[1].task").value("title"))
			.andExpect(jsonPath("$.progresses[1].user").value("testmail@softwareone.com"))
            .andExpect(jsonPath("$.progresses[2].newStatus").value("IN_PROGRESS"))
            .andExpect(jsonPath("$.progresses[2].task").value("title"))
			.andExpect(jsonPath("$.progresses[2].user").value("testmail@softwareone.com"));

    }

    
}
