package com.example.demo.domain;

import java.util.List;

import lombok.Data;

@Data
public class TaskList {

    private List<Task> tasks;    
    
    public int getCounter(){
        return this.tasks.size();
    }

}
