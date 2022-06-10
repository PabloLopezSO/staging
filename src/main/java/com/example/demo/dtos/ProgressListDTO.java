package com.example.demo.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressListDTO {

    List<ProgressDTO> progresses;
    
}
