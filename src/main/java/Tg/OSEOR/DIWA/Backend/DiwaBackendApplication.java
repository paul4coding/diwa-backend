package Tg.OSEOR.DIWA.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DiwaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiwaBackendApplication.class, args);
	}

}
