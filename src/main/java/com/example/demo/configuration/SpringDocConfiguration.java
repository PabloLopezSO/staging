package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

public class SpringDocConfiguration {
	
	  @Bean
	  public OpenAPI springShopOpenAPI() {
	      return new OpenAPI().info(
	    		  new Info().title("Todo App").contact(getContact())
		              .description("Exercise to learn")
		              .version("v0.0.1")
		              .license(new License().name("Apache 2.0").url("http://springdoc.org")))
		              	.externalDocs(new ExternalDocumentation()
			              .description("SpringShop Wiki Documentation")
			              .url("https://springshop.wiki.github.org/docs"));
	  }
	  
	  public Contact getContact() {
		  Contact contact = new Contact();
		  contact.setName("David Marciel");
		  contact.setEmail("david.marciel@softwareone.com");
		  contact.setUrl("softwareone.com");
		  
		  return contact;
	  }
}
