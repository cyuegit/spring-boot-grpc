package com.xcbeyond.springboot.grpc.client.grpc.loadbalancer;

import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;

/**
 * @ClassName: CustomLoadBalancerProvider
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 20:56
 */
public class CustomLoadBalancerProvider extends LoadBalancerProvider {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public String getPolicyName() {
        return "custom_round_robin";
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new CustomLoadBalancer(helper);
    }
}

