package com.xcbeyond.springboot.grpc.loadbalancer.roundrobin;

import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;

/**
 * @ClassName: CustomRoundLoadBalancerProvider
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 20:56
 */
public class CustomRoundLoadBalancerProvider extends LoadBalancerProvider {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getPolicyName() {
        return "custom_round_robin";
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new CustomRoundLoadBalancer(helper);
    }
}

