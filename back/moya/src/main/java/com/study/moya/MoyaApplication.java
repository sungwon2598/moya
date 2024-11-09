package com.study.moya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoyaApplication.class, args);
	}

}
