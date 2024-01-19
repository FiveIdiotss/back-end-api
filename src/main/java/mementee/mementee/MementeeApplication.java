package mementee.mementee;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		servers = {
@Server(url = "/", description = "Default Server url")
        }
				)
@SpringBootApplication
public class MementeeApplication { public static void main(String[] args) {
		SpringApplication.run(MementeeApplication.class, args);
	}
}
