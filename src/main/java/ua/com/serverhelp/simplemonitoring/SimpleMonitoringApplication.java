package ua.com.serverhelp.simplemonitoring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import ua.com.serverhelp.simplemonitoring.storage.DataBaseStorage;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SimpleMonitoringApplication {
	@Bean
	public Storage storage(){
		return new DataBaseStorage();
	}
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(4);
	}

	@Bean
	public HttpFirewall httpFirewall(){
		return new DefaultHttpFirewall();
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
		};
	}
	public static void main(String[] args) {
		SpringApplication.run(SimpleMonitoringApplication.class, args);
	}

}
