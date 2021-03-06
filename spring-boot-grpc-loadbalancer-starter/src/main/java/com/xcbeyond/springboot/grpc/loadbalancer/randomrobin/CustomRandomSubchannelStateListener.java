package com.xcbeyond.springboot.grpc.loadbalancer.randomrobin;

import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static io.grpc.ConnectivityState.*;

/**
 * @ClassName: CustomRandomSubchannelStateListener
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:05
 */
@Slf4j
class CustomRandomSubchannelStateListener implements LoadBalancer.SubchannelStateListener {
    private final LoadBalancer.Subchannel subchannel;
    private final LoadBalancer.Helper helper;
    private final CustomRandomLoadBalancer loadBalancer;

    public CustomRandomSubchannelStateListener(CustomRandomLoadBalancer customLoadBalancer,
                                               LoadBalancer.Subchannel subchannel,
                                               LoadBalancer.Helper helper) {
        this.loadBalancer = customLoadBalancer;
        this.subchannel = subchannel;
        this.helper = helper;
    }

    @Override
    public void onSubchannelState(ConnectivityStateInfo stateInfo) {
        CustomRandomRef<ConnectivityState> stateInfoRef = subchannel.getAttributes().get(CustomRandomLoadBalancer.STATE_INFO);
        ConnectivityState currentState = stateInfoRef.getValue();
        ConnectivityState newState = stateInfo.getState();

        log.info("{} 状态变化:{}", subchannel, newState);

        if (newState == SHUTDOWN) {
            log.info("关闭 {}", subchannel);
            //return;
        }

        if (newState == READY) {
            subchannel.requestConnection();
        }

        if (currentState == TRANSIENT_FAILURE) {
            if (newState == CONNECTING || newState == IDLE) {
                log.info("{} 建立连接或者失败", subchannel);
                //return;
            }
        }

        stateInfoRef.setValue(newState);
        updateLoadBalancerState();
    }

    private void updateLoadBalancerState() {
        List<LoadBalancer.Subchannel> readySubchannels = loadBalancer.getSubchannelMap()
                .values()
                .stream()
                .filter(s -> s.getAttributes().get(CustomRandomLoadBalancer.STATE_INFO).getValue() == READY)
                .collect(Collectors.toList());

        if (readySubchannels.isEmpty()) {
            log.info("更新 LB 状态为 CONNECTING，没有 READY 的 Subchannel");
            helper.updateBalancingState(CONNECTING, new CustomRandomSubchannelPicker(LoadBalancer.PickResult.withNoResult()));
        } else {
            log.info("更新 LB 状态为 READY，Subchannel 为:{}", readySubchannels.toArray());
            helper.updateBalancingState(READY, new CustomRandomSubchannelPicker(readySubchannels));
        }
    }
}


