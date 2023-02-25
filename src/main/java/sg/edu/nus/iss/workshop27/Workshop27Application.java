package sg.edu.nus.iss.workshop27;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import sg.edu.nus.iss.workshop27.repo.GameRepo;
import sg.edu.nus.iss.workshop27.service.GameService;

@SpringBootApplication
public class Workshop27Application {

	@Autowired
	GameRepo gameRepo;

	@Autowired
	GameService gameSvc;

	public static void main(String[] args) {
		SpringApplication.run(Workshop27Application.class, args);
	}

}
