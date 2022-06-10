package com.example.demo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TodoAppDomainTaskStatusTest {

    // test for TaskStatus.from(String arg) method

    @Test
    public void statusIsValid() {
        assertEquals(TaskStatus.DONE, TaskStatus.from("3"));
    }

    @Test
    public void statusIsNull() {
        String status = null;
        assertEquals(TaskStatus.INVALID, TaskStatus.from(status));
    }

    @Test
    public void statusIsEmpty() {
        assertEquals(TaskStatus.INVALID, TaskStatus.from(""));
    }

    @Test
    public void statusIsBlank() {
        assertEquals(TaskStatus.INVALID, TaskStatus.from(" "));
    }

    @Test
    public void statusIsNotNumeric() {
        assertEquals(TaskStatus.INVALID, TaskStatus.from("aaa"));
    }

    @Test
    public void statusIsNotInteger() {
        assertEquals(TaskStatus.INVALID, TaskStatus.from("2.5"));
    }
}