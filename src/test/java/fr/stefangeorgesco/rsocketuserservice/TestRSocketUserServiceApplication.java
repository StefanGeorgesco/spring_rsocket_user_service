package fr.stefangeorgesco.rsocketuserservice;

import org.springframework.boot.SpringApplication;

public class TestRSocketUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(RSocketUserServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
