package ro.unibuc.prodeng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateMovieRequest;
import ro.unibuc.prodeng.request.CreateTodoRequest;
import ro.unibuc.prodeng.request.CreateUserRequest;
import ro.unibuc.prodeng.service.TodoService;
import ro.unibuc.prodeng.service.UserService;
import ro.unibuc.prodeng.service.MovieService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
public class ProdEngApplication {

	@Autowired
	private UserService userService;

	@Autowired
	private TodoService todoService;

	@Autowired
	private MovieService movieService;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProdEngApplication.class, args);
	}

	@PostConstruct
	public void runAfterObjectCreated() {
			CreateMovieRequest movieRequest = new CreateMovieRequest("jaws","test","11","1",1,"test","test");
			movieService.createMovie(movieRequest);
	}
}
