package com.example.demo;

import com.example.demo.property.FileStorageProperties;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class TodoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoAppApplication.class, args);
	}

	@Bean
	public GroupedOpenApi toDoAppV1Group() {
		return GroupedOpenApi.builder().group("ToDoApp v1")
				.addOpenApiCustomiser(openApi -> openApi.info(new Info().title("ToDoApp v1").version("1.0")))
				.packagesToScan("com.example.demo")
				.pathsToExclude("/api/v2/**")
				.build();
	}

	@Bean
	public GroupedOpenApi toDoAppV2Group() {
		return GroupedOpenApi.builder().group("ToDoApp v2")
				.addOpenApiCustomiser(openApi -> openApi.info(new Info().title("ToDoApp v2").version("2.0")))
				.packagesToScan("com.example.demo")
				.pathsToExclude("/api/v1/**")
				.build();
	}

}
