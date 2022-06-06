package com.example.demo.controller;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="superUser")
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})
public class TodoAppControllerDownloadFileTest {

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
	void getFileByTaksIdandSlot1() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).file1("try.pdf").downloadFile1("https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf").build();
        todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks/1/files/1"))
            .andDo(print())
            .andExpect(status().isSeeOther())
            .andExpect(header().string("Location", "https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf"));
    }

    @Test
	void getFileByTaksIdandSlot2() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).file2("try.pdf").downloadFile2("https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf").build();
        todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks/1/files/2"))
            .andDo(print())
            .andExpect(status().isSeeOther())
            .andExpect(header().string("Location", "https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf"));
    }

    @Test
	void getFileByTaksIdandSlot3() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).file3("try.pdf").downloadFile3("https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf").build();
        todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks/1/files/3"))
            .andDo(print())
            .andExpect(status().isSeeOther())
            .andExpect(header().string("Location", "https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf"));
    }

    @Test
	void getSlotError() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).file1("try.pdf").downloadFile1("https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf").build();
        todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks/1/files/6"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
	void getTaskIdNotExist() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).file1("try.pdf").downloadFile1("https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf").build();
        todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks/8/files/1"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }

    @Test
	void getSlotIsEmpty() throws Exception{

        User user1 = User.builder().mail("testmail@softwareone.com").build();
		userRepository.save(user1);

        Task task1 = Task.builder().title("title 1").description("description 1").dueDate(LocalDateTime.now()).createdDate(LocalDateTime.now()).status(1).creator(1).file1("try.pdf").downloadFile1("https://emeaacc2022tjavastoacc.blob.core.windows.net/task1/0ea0a77c-8431-43c0-bc31-ee283b499a79_try.pdf").build();
        todoAppRepository.save(task1);

        mockMvc.perform(get("/api/tasks/1/files/2"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
    
    
}
