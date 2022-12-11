package ws.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ws.core.service.FirebaseService;
import ws.core.service.InitProjectService;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@PropertySource(value = {"classpath:application.properties"},encoding = "UTF-8")
@EnableScheduling
public class WsCoreApplication implements WebMvcConfigurer, CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(WsCoreApplication.class, args);
	}

	@Autowired
	private InitProjectService initProjectService;
	
	@Autowired
	private FirebaseService firebaseService;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Current JVM version - " + System.getProperty("java.version"));
		 
		/* Init project */
		initProjectService.installDataIfNotExists();
		
		/* Init firebase */
		firebaseService.InitFCMService();
	}
}
