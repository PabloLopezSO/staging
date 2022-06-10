package com.example.demo.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import javax.servlet.FilterChain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.example.demo.dtos.UserCredentialsDTO;
import com.example.demo.repository.TaskRepository;
import com.example.demo.security.JwtTokenFilter;
import com.example.demo.service.LoginService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;



@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})
class TodoAppControllerDoLoginTest {
    
    @Autowired
	private MockMvc mockMvc;
	
	@Autowired
	TaskRepository todoAppRepository;

    @Mock
    private TaskRepository mockTaskRepo;

    @MockBean
    private LoginService loginService;

    @MockBean
    private UserCredentialsDTO userCredentialsDTO;

	@BeforeEach
	void setUp() {
		todoAppRepository.deleteAll();
    }
       

    @Test
	void testFilter() throws Exception {

        JwtTokenFilter filter = new JwtTokenFilter();
 
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockResp = Mockito.mock(HttpServletResponse.class);
        FilterChain mockFilterChain = Mockito.mock(FilterChain.class);

        Mockito.when(mockReq.getRequestURI()).thenReturn("/api/login");
        Mockito.when(loginService.validateToken(ArgumentMatchers.anyString())).thenReturn(true);
        
        filter.doFilter(mockReq, mockResp, mockFilterChain);
        filter.destroy();

    }

    @Test
	void testDoLoginInvalidCredentials() throws Exception {

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
	void testDoLoginUnauthorized() throws Exception {

        mockMvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }
}
