package net.javaguides.springboot_backend;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
public class SpringbootBackendApplication {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootBackendApplication.class, args);
	}

	@PostConstruct
	public void logDatasourceUrl() throws SQLException {
		System.out.println("🚨 Connected DB URL: " + dataSource.getConnection().getMetaData().getURL());
	}
}
