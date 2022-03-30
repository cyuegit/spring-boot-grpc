package com.xcbeyond.springboot.grpc.loadbalancer.weightroundrobin;

import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;

/**
 * @ClassName: CustomWeightRoundLoadBalancerProvider
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 20:56
 */
public class CustomWeightRoundLoadBalancerProvider extends LoadBalancerProvider {

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
        return "custom_weight_round_robin";
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new CustomWeightRoundLoadBalancer(helper);
    }
}

