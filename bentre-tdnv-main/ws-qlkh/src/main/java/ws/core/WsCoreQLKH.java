package ws.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ws.core.service.QLKHService;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@PropertySource(value = {"classpath:application.properties"},encoding = "UTF-8")
@EnableScheduling
public class WsCoreQLKH implements WebMvcConfigurer, CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(WsCoreQLKH.class, args);
	}

	@Autowired
	protected QLKHService qlkhService;
	
	@Override
	public void run(String... args) throws Exception {
		
	}
	
}
