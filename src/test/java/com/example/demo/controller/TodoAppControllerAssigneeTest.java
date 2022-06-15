package com.example.demo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoginService;
import com.example.demo.service.TaskService;

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
class TodoAppControllerAssigneeTest {

    @Autowired
	private MockMvc mockMvc;
	
	@Autowired
	TaskRepository todoAppRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TaskService taskService;

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
	void testAssigneeCorrect() throws Exception {
		LocalDateTime date = LocalDateTime.of(2026, 4, 21, 10, 00, 27);
		User user = User.builder().id(1).mail("testmail@softwareone.com").build();
		userRepository.save(user);
		Task task = Task.builder().title("title33333").description("description").dueDate(date).createdDate(LocalDateTime.now()).status(1).creator(1).assignee(1).build();
		todoAppRepository.save(task);

		String taskPatch = "{\"title\": \"title patched\", \"description\": \"description patched\", \"status\": 3, \"dueDate\":\"2023-02-02 10:00:27\"}";

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);	

		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1/assignee/testmail@softwareone.com").header("Authorization", token).contentType("application/json").content(taskPatch))
			.andExpect(status().isOk());
            
	}

    @Test
    void testAssigneeIncorrectUserDontExists() throws Exception {
		LocalDateTime date = LocalDateTime.of(2026, 4, 21, 10, 00, 27);
		User user = User.builder().id(1).mail("testmail@softwareone.com").build();
		userRepository.save(user);
		Task task = Task.builder().title("title33333").description("description").dueDate(date).createdDate(LocalDateTime.now()).status(1).creator(1).assignee(1).build();
		todoAppRepository.save(task);

		String taskPatch = "{\"title\": \"title patched\", \"description\": \"description patched\", \"status\": 3, \"dueDate\":\"2023-02-02 10:00:27\"}";

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);	

		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1/assignee/testmail2@softwareone.com").header("Authorization", token).contentType("application/json").content(taskPatch))
			.andExpect(status().isBadRequest());
            
	}

    @Test
    void testAssigneeIncorrectTaskDontExists() throws Exception {
		LocalDateTime date = LocalDateTime.of(2026, 4, 21, 10, 00, 27);
		User user = User.builder().id(1).mail("testmail@softwareone.com").build();
		userRepository.save(user);
		Task task = Task.builder().title("title33333").description("description").dueDate(date).createdDate(LocalDateTime.now()).status(1).creator(1).assignee(1).build();
		todoAppRepository.save(task);

		String taskPatch = "{\"title\": \"title patched\", \"description\": \"description patched\", \"status\": 3, \"dueDate\":\"2023-02-02 10:00:27\"}";

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);	

		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/11/assignee/testmail@softwareone.com").header("Authorization", token).contentType("application/json").content(taskPatch))
			.andExpect(status().isNotFound());
            
	}
    
	
}
   