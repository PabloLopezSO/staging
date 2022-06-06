package com.example.demo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.domain.Task;
import com.example.demo.domain.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.FileService;
import com.example.demo.service.LoginService;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({ "/drop.sql", "/create.sql", "/testDataOnlyTaskStatuses.sql" })
class TodoAppControllerUploadFile {

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

    @Autowired
    private MockMvc mvc;

    @Autowired
	UserRepository userRepository;
    private WebApplicationContext webApplicationContext;

    @MockBean
    private FileService fileService;

    @Test
    void upload() throws Exception {
        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);
		
		Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).build();
		todoAppRepository.save(task1);

        String fileName = "fineuploader.png";
        MockMultipartFile multipartFile =
        new MockMultipartFile("qqfile", fileName, "image/jpg", "Some bytes".getBytes());
        
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // mockMvc.perform(MockMvcRequestBuilders.multipart("/tasks/1/files/1")
            // .file(multipartFile))
            // .andExpect(status().is(200));
        mockMvc.perform(patch("/tasks/1/files/1",multipartFile))
        .andExpect(status().is(200));
    }
}
