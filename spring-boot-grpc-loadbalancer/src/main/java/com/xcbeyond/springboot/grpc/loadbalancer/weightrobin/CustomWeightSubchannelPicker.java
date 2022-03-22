package com.xcbeyond.springboot.grpc.loadbalancer.weightrobin;

import io.grpc.LoadBalancer;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: CustomRoundSubchannelPicker
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:06
 */
@Slf4j
class CustomWeightSubchannelPicker extends LoadBalancer.SubchannelPicker {

    private final AtomicInteger index = new AtomicInteger();

    private List<LoadBalancer.Subchannel> subchannelList;

    private LoadBalancer.PickResult pickResult;

    public CustomWeightSubchannelPicker(LoadBalancer.PickResult pickResult) {
        this.pickResult = pickResult;
    }

    public CustomWeightSubchannelPicker(List<LoadBalancer.Subchannel> subchannelList) {
        this.subchannelList = subchannelList;
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
        if (index.get() >= subchannelList.size()) {
            index.set(0);
        }

        LoadBalancer.Subchannel subchannel = subchannelList.get(index.getAndIncrement());
        try {
            Filed outerField = subchannel.getClass().getDeclaredField("this$0");
            //this$0是私有的,提升访问权限
            outerField.setAccessible(true);
            LoadBalancer.Subchannel o = (LoadBalancer.Subchannel)outerField.get(subchannel);
        } catch (Exception e) {
            log.info("custom_weight_robin nextSubchannel:{}", subchannel);
            e.printStackTrace();
        }
        log.info("返回 Subchannel:{}", subchannel);
        return LoadBalancer.PickResult.withSubchannel(subchannel);
    }
}

