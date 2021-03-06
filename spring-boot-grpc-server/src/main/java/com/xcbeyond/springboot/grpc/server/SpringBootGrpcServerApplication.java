package com.xcbeyond.springboot.grpc.server;

import net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		GrpcDiscoveryClientAutoConfiguration.class
})
//@EnableEurekaClient
//@EnableDiscoveryClient
public class SpringBootGrpcServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootGrpcServerApplication.class, args);
	}

}
