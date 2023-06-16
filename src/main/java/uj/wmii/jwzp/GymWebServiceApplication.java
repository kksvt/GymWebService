package uj.wmii.jwzp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymWebServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymWebServiceApplication.class, args);
	}

}
