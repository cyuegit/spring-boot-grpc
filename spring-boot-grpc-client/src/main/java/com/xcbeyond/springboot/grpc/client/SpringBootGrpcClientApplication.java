package com.xcbeyond.springboot.grpc.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableEurekaClient
//@EnableDiscoveryClient
public class SpringBootGrpcClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootGrpcClientApplication.class, args);
	}

}
