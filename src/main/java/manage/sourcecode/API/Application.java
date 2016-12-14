package manage.sourcecode.API;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Matthieu MERRHEIM
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

	// Lance l'application
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
