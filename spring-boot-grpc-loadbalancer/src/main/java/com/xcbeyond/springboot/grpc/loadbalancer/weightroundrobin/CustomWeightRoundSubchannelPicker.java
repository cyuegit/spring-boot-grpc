package com.xcbeyond.springboot.grpc.loadbalancer.weightroundrobin;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName: CustomWeightRoundSubchannelPicker
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:06
 */
@Slf4j
class CustomWeightRoundSubchannelPicker extends LoadBalancer.SubchannelPicker {

    private final AtomicInteger index = new AtomicInteger();

    private LoadBalancer.PickResult pickResult;

    private Map<String, String> weightMap;

    private Map<String, LoadBalancer.Subchannel> serverMap;

    // 真实服务列表
    private List<LoadBalancer.Subchannel> subchannelList;

    //按权重累计的服务列表
    private List<LoadBalancer.Subchannel> weightSubchannelList;

    public CustomWeightRoundSubchannelPicker(LoadBalancer.PickResult pickResult) {
        this.pickResult = pickResult;
    }

    public CustomWeightRoundSubchannelPicker(List<LoadBalancer.Subchannel> subchannelList) {
        this.subchannelList = subchannelList;
        if (subchannelList.size() == 1) {
            this.weightSubchannelList = subchannelList;
            return;
        }
        extracted(subchannelList);
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
        if (weightSubchannelList.size() == 1) {
            log.info("only one Subchannel");
            return LoadBalancer.PickResult.withSubchannel(weightSubchannelList.get(0));
        }

        if (index.get() >= weightSubchannelList.size()) {
            index.set(0);
        }
        LoadBalancer.Subchannel subchannel = weightSubchannelList.get(index.getAndIncrement());
        log.info("返回 Subchannel:{}", subchannel);
        return LoadBalancer.PickResult.withSubchannel(subchannel);
    }

    /**
     * {
     *     "metadata": {
     *         "nacos.instanceId": "192.168.222.1#5390#shanghai#demo@@spring-boot-grpc-server",
     *         "nacos.ephemeral": "true",
     *         "nacos.healthy": "true",
     *         "nacos.weight": "1.0",
     *         "nacos.cluster": "shanghai",
     *         "preserved.register.source": "SPRING_CLOUD",
     *         "gRPC_port": "8877"
     *     },
     *     "secure": false,
     *     "port": 5390,
     *     "host": "192.168.222.1",
     *     "serviceId": "spring-boot-grpc-server"
     * }
     * @param subchannelList
     * @return null
     * @author chenglong.yue <chenglong.yue@idiaoyan.com>
     * @date 2022/3/23 17:44
     */
    private void extracted(List<LoadBalancer.Subchannel> subchannelList) {
        LoadBalancer.Subchannel first = subchannelList.get(0);
        try {
            Object fefobj = ReflectUtil.getFieldValue(first, "this$0");
            Object nameResolver = ReflectUtil.getFieldValue(fefobj, "nameResolver");
            Object instanceList = ReflectUtil.getFieldValue(nameResolver, "instanceList");
            JSONArray array = JSONUtil.parseArray(instanceList);
            if (array.size() != subchannelList.size()) {
                return;
            }
            log.info("nacos instanceList:{}", JSONUtil.toJsonStr(array));
            weightMap = new ConcurrentHashMap<>();
            serverMap = new ConcurrentHashMap<>();
            weightSubchannelList = new ArrayList<>();
            // nacos 元数据得到server=>weight的映射
            for (JSONObject ob : array.jsonIter()) {
                String host = ob.getStr("host");
                JSONObject metadata = ob.getJSONObject("metadata");
                double weight = metadata.getDouble("nacos.weight");
                if (weight <= 0) {
                    continue;
                }
                if (weight <= 1) {
                    weight = 1;
                }
                // 这里限制最大权重10，不然循环慢，效率低
                if (weight > 10) {
                    weight = 10;
                }
                String grpcPort = metadata.getStr("gRPC_port");
                String tmp = String.valueOf(weight);
                // server=>weight
                weightMap.put(host.concat(":").concat(grpcPort), tmp.substring(0, tmp.indexOf(".")));
            }
            // 得到server=》Subchannel 关系
            for (LoadBalancer.Subchannel sub : subchannelList) {
                EquivalentAddressGroup eg = sub.getAddresses();
                String ip = ((InetSocketAddress) eg.getAddresses().get(0)).getAddress().getHostAddress();
                int port = ((InetSocketAddress) eg.getAddresses().get(0)).getPort();
                serverMap.put(ip.concat(":").concat(String.valueOf(port)), sub);
            }
            Set<String> keySet = weightMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String server = iterator.next();
                int weight = Integer.valueOf(weightMap.get(server));
                for (int i = 0; i < weight; i++) {
                    weightSubchannelList.add(serverMap.get(server));
                }
            }
        } catch (Exception e) {
            log.error("CustomWeightRandomSubchannelPicker error:", e.getMessage());
            e.printStackTrace();
        }
    }
}

