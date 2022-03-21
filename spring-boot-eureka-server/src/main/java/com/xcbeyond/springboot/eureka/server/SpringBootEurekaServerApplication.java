package com.xcbeyond.springboot.eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author been
 */
@SpringBootApplication
@EnableEurekaServer
public class SpringBootEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEurekaServerApplication.class, args);
	}

}
