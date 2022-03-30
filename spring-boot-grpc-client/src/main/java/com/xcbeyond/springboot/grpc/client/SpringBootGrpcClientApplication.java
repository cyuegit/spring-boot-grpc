package com.xcbeyond.springboot.grpc.client;

import net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		GrpcDiscoveryClientAutoConfiguration.class
})
//@EnableEurekaClient
//@EnableDiscoveryClient
public class SpringBootGrpcClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootGrpcClientApplication.class, args);
	}

}
