package com.example.demo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.io.File;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileService;
import com.example.demo.service.LoginService;

import io.jsonwebtoken.lang.Assert;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="superUser")
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})
class TodoAppControllerUploadFileTest {

	
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

    @Test
    public void testNotValid1() throws Exception {

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task1);


        MockMultipartFile file = new MockMultipartFile("file", "dummy.csv",
        "text/csv", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/1");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNotValid2() throws Exception {

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task1);


        MockMultipartFile file = new MockMultipartFile("file", "dummy.txt",
        "text/txt", "".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/1");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNotValidId() throws Exception {

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task1);


        MockMultipartFile file = new MockMultipartFile("file", "dummy.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/4");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file))
                .andExpect(status().is(400));
    }

    @Test
	void recordDeletedIncorrect() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        mockMvc.perform(delete("/api/tasks/1/files/1"))
            .andExpect(status().isBadRequest());
		
	}

    @Test
	void recordDeletedCorrect2() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/2");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        mockMvc.perform(delete("/api/tasks/1/files/2"))
            .andExpect(status().isOk());
		
	}

    @Test
	void recordDeletedCorrect3() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/3");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        mockMvc.perform(delete("/api/tasks/1/files/3"))
            .andExpect(status().isOk());
		
	}

    @Test
	void recordDeletedCorrect1() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/1");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        mockMvc.perform(delete("/api/tasks/1/files/1"))
            .andExpect(status().isOk());
		
	}

    @Test
	void recordDeletedCorrectAll() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        String taskUpdate = "{\"status\": 3}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").contentType("application/json").content(taskUpdate));

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/4/files/1");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isOk());
		
	}

    @Test
    void recordDeletedCorrectName1() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        String taskUpdate = "{\"status\": 3}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").contentType("application/json").content(taskUpdate));

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/1");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        String taskDelete = "{\"filename\": \"dummy1.txt\"}";

        mockMvc.perform(delete("/api/tasks/1").contentType("application/json").content(taskDelete))
            .andExpect(status().isOk());
		
	}

    @Test
    void recordDeletedCorrectName2() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        String taskUpdate = "{\"status\": 3}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").contentType("application/json").content(taskUpdate));

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/2");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        String taskDelete = "{\"filename\": \"dummy1.txt\"}";

        mockMvc.perform(delete("/api/tasks/1").contentType("application/json").content(taskDelete))
            .andExpect(status().isOk());
		
	}

    @Test
    void recordDeletedCorrectName3() throws Exception {
		
		User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

		Task task1 = Task.builder().title("title 2").description("description 2").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(3).creator(1).build();
		todoAppRepository.save(task1);

        String taskUpdate = "{\"status\": 3}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/tasks/1").contentType("application/json").content(taskUpdate));

        MockMultipartFile file = new MockMultipartFile("file", "dummy1.txt",
        "text/plain", "Some dataset...".getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/tasks/1/files/3");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PATCH");
                return request;
            }
        });
        mockMvc.perform(builder
                .file(file));

        String taskDelete = "{\"filename\": \"dummy1.txt\"}";

        mockMvc.perform(delete("/api/tasks/1").contentType("application/json").content(taskDelete))
            .andExpect(status().isOk());
		
	}

}
    
