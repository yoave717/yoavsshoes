package com.shoestore;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application class for Shoe Store E-commerce system
 *
 * This class serves as the entry point for the application and configures
 * JPA repositories, entity scanning, and auditing capabilities.
 */
@SpringBootApplication(scanBasePackages = "com.shoestore")
@EnableJpaRepositories(basePackages = "com.shoestore.repository")
@EntityScan(basePackages = "com.shoestore.entity")
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class ShoeStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoeStoreApplication.class, args);
	}
}
