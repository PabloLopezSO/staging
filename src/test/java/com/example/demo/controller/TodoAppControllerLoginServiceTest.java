package com.example.demo.controller;

import com.example.demo.dtos.UserCredentialsDTO;
import com.example.demo.service.LoginService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;


import org.springframework.security.authentication.BadCredentialsException;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/drop.sql","/create.sql","/testDataOnlyTaskStatuses.sql"})
class TodoAppControllerLoginServiceTest {
    

    
    @Test
    void validateTokenInvalidToken() throws Exception{

        LoginService loginService = new LoginService();

        BadCredentialsException thrown = Assertions.assertThrows(BadCredentialsException.class, () -> {
           loginService.validateToken("invalidToken");
        }, "Invalid Credentials Exception");

        Assertions.assertEquals("INVALID_CREDENTIALS", thrown.getMessage());

    }

    @Test
    void validateTokenExpire() throws Exception{

        LoginService loginService = new LoginService();

        loginService.modulusExponentPublicKey();

        BadCredentialsException thrown = Assertions.assertThrows(BadCredentialsException.class, () -> {
           loginService.validateToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6ImpTMVhvMU9XRGpfNTJ2YndHTmd2UU8yVnpNYyJ9.eyJhdWQiOiJlZjVlYzAwOC1kZjhjLTRhZjgtYmI1ZC01ZWVmMmNiZWM0NDEiLCJpc3MiOiJodHRwczovL2xvZ2luLm1pY3Jvc29mdG9ubGluZS5jb20vOGQ5OTM0YWMtYjRkMC00YWU2LTllNzUtMGI1NGRiZjZhNGJiL3YyLjAiLCJpYXQiOjE2NTMwMzIxMjQsIm5iZiI6MTY1MzAzMjEyNCwiZXhwIjoxNjUzMDM2MDI0LCJhaW8iOiJBVVFBdS84VEFBQUFKNXpwd2FtWkFsVFhuejRXcHc2WGFhdkt0WTNKU3ZlWjRMN0VGOXRhT3p3YzkzblJDOXF2cGpHMndiR05teUo5cnBkcjJZUVJQbmdnRUxvRE9MWFgzQT09IiwiaWRwIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvMWRjOWIzMzktZmFkYi00MzJlLTg2ZGYtNDIzYzM4YTBmY2I4LyIsIm5hbWUiOiJMb3BleiwgTHVpcyIsIm9pZCI6ImY2MWZlMmQ3LTgwYmMtNDA2MS04YTE4LTVlNmVmZDVmYTZhYiIsInByZWZlcnJlZF91c2VybmFtZSI6Imx1aXMubG9wZXpAc29mdHdhcmVvbmUuY29tIiwicmgiOiIwLkFZRUFyRFNaamRDMDVrcWVkUXRVMl9ha3V3akFYdS1NM19oS3UxMWU3eXkteEVHQkFDNC4iLCJzdWIiOiJsc2ZHMlVLaEZKaFdlQzRDcU5Da3MxSkgwSzJraHp2SWxiLWhoUXRMYzlFIiwidGlkIjoiOGQ5OTM0YWMtYjRkMC00YWU2LTllNzUtMGI1NGRiZjZhNGJiIiwidXRpIjoiTHlvT0JqSFpSa21KN0VJU0ZQZzhBQSIsInZlciI6IjIuMCJ9.A9VPUW2yDbSAMRCarE043Qcogtr4Bw7pQq0n1QuQx9ubkG6N9jOlQhsmuBTGYdmpM4EQvC6Ma-hDQ8al-u7yLa-MzYtp5EVuXSfd5AMqfKcIrYrVQjYr63AnAgLKZdVAeUGfjTiPAo5SuoQYjCKROB6iZkWLNDrNyMVlb_KIIflpMb9YYyY_JPEeoFcv-3zk5sumG8HJYWFa7yHlyW3hLkezjQhMv38DsNn-IEFB3ObTiij9EiM6oG8M8lfUAYCp6V3_0qgq0vTRJSFCdQQY9kE_j0srTt17Zj-WCYhTmRhBIhyPfqQ19eYoja-BbbkoVOfPDFs5L95BrXM3-8_kyA");
        }, "Token Has Expired Exception");

        Assertions.assertEquals("Token has Expired", thrown.getMessage());


    }
    @Test
    void loginServiceConnection() throws Exception{

        LoginService loginService = new LoginService("registrationClientId", "registrationClientSecret", "registrationClientScope", "registrationClientGrantType");
        Assertions.assertNotNull(loginService);
    }

    @Test
    void loginUserByToken() throws Exception{

        LoginService loginService = new LoginService();
        UserCredentialsDTO userCredentials = new UserCredentialsDTO("username", "password");
        Assertions.assertThrows(HttpClientErrorException.class, () -> {
            loginService.loginUserByToken(userCredentials);
         }, "Bad Request 400");

        Assertions.assertNotNull(loginService);
       
    }
}
