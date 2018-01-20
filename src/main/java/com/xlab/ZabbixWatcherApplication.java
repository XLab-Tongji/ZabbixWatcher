package com.xlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAutoConfiguration
public class ZabbixWatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZabbixWatcherApplication.class, args);
	}
}
