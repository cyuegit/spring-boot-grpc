package com.xcbeyond.springboot.grpc.loadbalancer.weightrobin;

import cn.hutool.*;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import io.grpc.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Field;

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
            Object fefobj = ReflectUtil.getFieldValue(subchannel, "this$0");
            Object nameResolver = ReflectUtil.getFieldValue(fefobj, "nameResolver");
            Object instanceList = ReflectUtil.getFieldValue(nameResolver, "instanceList");
            System.out.println(JSONUtil.toJsonStr(instanceList));
        } catch (Exception e) {
            log.error("custom_weight_robin nextSubchannel:{}", subchannel);
            e.printStackTrace();
        }
        log.info("返回 Subchannel:{}", subchannel);
        return LoadBalancer.PickResult.withSubchannel(subchannel);
    }
}

