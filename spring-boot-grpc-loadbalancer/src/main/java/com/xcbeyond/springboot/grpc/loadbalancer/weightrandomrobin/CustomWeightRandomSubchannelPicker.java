package com.xcbeyond.springboot.grpc.loadbalancer.weightrandomrobin;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import io.grpc.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: CustomWeightRandomSubchannelPicker
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:06
 */
@Slf4j
class CustomWeightRandomSubchannelPicker extends LoadBalancer.SubchannelPicker {

    private final AtomicInteger index = new AtomicInteger();

    private List<LoadBalancer.Subchannel> subchannelList;

    private LoadBalancer.PickResult pickResult;

    private Map<String, LoadBalancer.Subchannel> subchannelMap;

    public CustomWeightRandomSubchannelPicker(LoadBalancer.PickResult pickResult) {
        this.pickResult = pickResult;
    }

    public CustomWeightRandomSubchannelPicker(List<LoadBalancer.Subchannel> subchannelList) {
        this.subchannelList = subchannelList;
        //TODO 更新subchannelMap
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
        LoadBalancer.Subchannel first = subchannelList.get(0);
        try {
            Object fefobj = ReflectUtil.getFieldValue(first, "this$0");
            Object nameResolver = ReflectUtil.getFieldValue(fefobj, "nameResolver");
            JSONArray instanceList = (JSONArray)ReflectUtil.getFieldValue(nameResolver, "instanceList");
            log.info("nacos instanceList:{}", JSONUtil.toJsonStr(instanceList));
        } catch (Exception e) {
            log.error("custom_weight_robin nextSubchannel:{}", first);
            e.printStackTrace();
        }
        log.info("返回 Subchannel:{}", first);
        return LoadBalancer.PickResult.withSubchannel(first);
    }
}

