package com.example.demo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LoginService;
import com.example.demo.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
class TodoAppControllerPatchTaskTest {

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
	void testCompletePatchIsCorrect() throws Exception {
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

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").header("Authorization", token).contentType("application/json").content(taskPatch))
			.andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("title patched"))
            .andExpect(jsonPath("$.description").value("description patched"))
            .andExpect(jsonPath("$.status").value("3"))
            .andExpect(jsonPath("$.dueDate").value("2023-02-02 10:00:27"));
            
	}

	@Test
	void testPatchIdIsNotFound() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		Task task = Task.builder().title("title").description("description").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task);

        String taskPatch = "{\"title\": \"title patched\", \"description\": \"description patched\", \"status\": 3, \"dueDate\":\"2023-02-02 10:00:27\" }";

        Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);		
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/7").header("Authorization", token).contentType("application/json").content(taskPatch))
            .andExpect(status().isNotFound());
		
	}
	
	@ParameterizedTest
	@MethodSource("endPointProvider")
	void testPatchIsBadRequest(String endPoint, String taskDetails) throws Exception{

		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		Task task1 = Task.builder().title("title").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);

		String taskPatch = taskDetails;

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");	
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);	
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

		mockMvc.perform(MockMvcRequestBuilders.patch(endPoint).header("Authorization", token).contentType("application/json").content(taskPatch))
			.andExpect(status().isBadRequest());
			
	}

	static Stream<Arguments> endPointProvider() {
		return Stream.of(
			// ID Mismatch
			Arguments.of("/api/tasks/s","{\"title\": \"title patched\"}"),
			// Not unique title
			Arguments.of("/api/tasks/1","{\"title\": \"title\"}"),
			// Invalid status number			
			Arguments.of("/api/tasks/1","{\"status\": 5}"),
			// Invalid status format
			Arguments.of("/api/tasks/1","{\"status\": \"a\"}"),
			// DueDate is later than current date
			Arguments.of("/api/tasks/1","{\"dueDate\":\"2021-02-02 10:00:27\"}")
		);
	}

	@ParameterizedTest
	@MethodSource("expectedResultProvider")
	void testPatchIsCorrect(String taskDetails, String attribute, String expectedResult) throws Exception{

		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		Task task1 = Task.builder().title("title").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task1);

		String taskPatch = taskDetails;

		Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail@softwareone.com");
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.TRUE);		
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").header("Authorization", token).contentType("application/json").content(taskPatch))
            .andExpect(jsonPath("$."+attribute).value(expectedResult));
	}

	static Stream<Arguments> expectedResultProvider() {
		return Stream.of(
			// Expected title is correct
			Arguments.of("{\"title\": \"title patched\"}","title","title patched"),
			// Expected description is correct
			Arguments.of("{\"description\": \"description patched\"}","description","description patched"),
			// Expected status is correct			
			Arguments.of("{\"status\": 3}","status","3"),
			// Expected dueDate is correct
			Arguments.of("{\"dueDate\":\"2023-02-02 10:00:27\"}","dueDate","2023-02-02 10:00:27")
		);
	} 
	@Test
	void testUserIsNotCreatorOrAssegneePatch() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		Task task = Task.builder().title("title").description("description").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();

		todoAppRepository.save(task);

        String taskPatch = "{\"title\": \"title patched\", \"description\": \"description patched\", \"status\": 3, \"dueDate\":\"2023-02-02 10:00:27\" }";

        Mockito.when(ls.validateToken(ArgumentMatchers.anyString())).thenReturn(Boolean.TRUE);
		Mockito.when(ls.getUsernameFromToken(ArgumentMatchers.any())).thenReturn("testmail2@softwareone.com");
		Mockito.when(ts.validateCreatorOrAssignee(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(Boolean.FALSE);		
		
		String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";	

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").header("Authorization", token).contentType("application/json").content(taskPatch))
            .andExpect(status().isBadRequest());
		
	} 
}
   