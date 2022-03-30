package com.xcbeyond.springboot.grpc.loadbalancer.randomrobin;

import io.grpc.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: CustomRandomSubchannelPicker
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:06
 */
@Slf4j
class CustomRandomSubchannelPicker extends LoadBalancer.SubchannelPicker {

    private final AtomicInteger index = new AtomicInteger();

    private List<LoadBalancer.Subchannel> subchannelList;

    private LoadBalancer.PickResult pickResult;

    public CustomRandomSubchannelPicker(LoadBalancer.PickResult pickResult) {
        this.pickResult = pickResult;
    }

    public CustomRandomSubchannelPicker(List<LoadBalancer.Subchannel> subchannelList) {
        this.subchannelList = subchannelList;
        log.info("subchannelList size:{}", subchannelList.size());
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        if (pickResult != null) {
            log.info("有错误的 pickResult，返回:{}", pickResult);
            return pickResult;
        }
        LoadBalancer.PickResult pickResult = nextSubchannel(args);
        log.info("Pick 下一个 Subchannel:{}", pickResult.getSubchannel());
        return pickResult;
    }

    private LoadBalancer.PickResult nextSubchannel(LoadBalancer.PickSubchannelArgs args) {
        // com.alibaba.nacos.client.naming.utils.Chooser 参考 ThreadLocalRandom.current().nextInt(10)
        log.info("nextSubchannel size:{}", subchannelList.size());
        if (subchannelList.size() == 1) {
            return LoadBalancer.PickResult.withSubchannel(subchannelList.get(0));
        }
        int pos = ThreadLocalRandom.current().nextInt(subchannelList.size());
        LoadBalancer.Subchannel subchannel = subchannelList.get(pos);
        log.info("返回 Subchannel:{}", subchannel);
        return LoadBalancer.PickResult.withSubchannel(subchannel);
    }
}

