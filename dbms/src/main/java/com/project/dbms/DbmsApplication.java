package com.project.dbms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ServletComponentScan
@SpringBootApplication
public class DbmsApplication {

	
	public static void main(String[] args) {
		
		SpringApplication.run(DbmsApplication.class, args);
	}

	
}
