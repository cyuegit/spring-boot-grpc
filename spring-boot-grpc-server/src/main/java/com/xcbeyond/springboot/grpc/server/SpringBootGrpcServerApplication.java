package com.xcbeyond.springboot.grpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableEurekaClient
//@EnableDiscoveryClient
public class SpringBootGrpcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootGrpcServerApplication.class, args);
	}

}
