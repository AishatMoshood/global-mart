package com.verraki.globalmart.stockmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GlobalMartApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlobalMartApplication.class, args);
	}

}
