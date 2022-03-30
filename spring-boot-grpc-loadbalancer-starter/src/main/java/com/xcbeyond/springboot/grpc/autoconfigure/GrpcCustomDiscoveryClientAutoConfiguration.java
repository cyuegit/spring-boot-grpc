package com.xcbeyond.springboot.grpc.autoconfigure;

import com.xcbeyond.springboot.grpc.nameresolver.CustomDiscoveryClientResolverFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(DiscoveryClient.class)
public class GrpcCustomDiscoveryClientAutoConfiguration {

    @ConditionalOnMissingBean
    @Lazy
    @Bean
    CustomDiscoveryClientResolverFactory grpcCustomDiscoveryClientResolverFactory(final DiscoveryClient client) {
        return new CustomDiscoveryClientResolverFactory(client);
    }
}
