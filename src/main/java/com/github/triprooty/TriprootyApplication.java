package com.github.triprooty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TriprootyApplication {

	public static void main(String[] args) {

		SpringApplication.run(TriprootyApplication.class, args);
	}

}
