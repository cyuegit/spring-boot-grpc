package com.xcbeyond.springboot.grpc.loadbalancer.weightroundrobin;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.grpc.ConnectivityState.IDLE;

/**
 * @ClassName: CustomWeightRoundLoadBalancer
 * @Description: 权重轮询
 * @Author: chenglong.yue
 * @Date: 2022/3/20 20:59
 */
@Slf4j
public class CustomWeightRoundLoadBalancer extends LoadBalancer {

    public static final Attributes.Key<CustomWeightRoundRef<ConnectivityState>> STATE_INFO = Attributes.Key.create("state-info");

    private final Helper helper;

    Map<EquivalentAddressGroup, Subchannel> subchannelMap = new ConcurrentHashMap<>();

    public CustomWeightRoundLoadBalancer(Helper helper) {
        this.helper = helper;
    }

    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        log.info("处理地址:{}", resolvedAddresses.getAddresses().toString());

        // 将解析的地址分割成单个 Address
        List<EquivalentAddressGroup> latestAddresses = resolvedAddresses.getAddresses()
                .stream()
                .flatMap(this::splitAddressCollection)
                .distinct()
                .collect(Collectors.toList());

        // 已经存在的地址
        Set<EquivalentAddressGroup> originAddresses = subchannelMap.keySet();

        // 对新的 Address 创建 Subchannel
        Map<EquivalentAddressGroup, Subchannel> newSubchannelMap = latestAddresses.stream()
                .filter(e -> !originAddresses.contains(e))
                .map(this::buildCreateSubchannelArgs)
                .map(helper::createSubchannel)
                .map(this::processSubchannel)
                .collect(Collectors.toConcurrentMap(Subchannel::getAddresses, s -> s));

        // 将已存在的 Subchannel 放到新的集合中
        originAddresses.stream()
                .filter(latestAddresses::contains)
                .forEach(e -> newSubchannelMap.put(e, subchannelMap.get(e)));


        // 关闭需要移除的 Subchannel
        originAddresses.stream()
                .filter(e -> !latestAddresses.contains(e))
                .map(e -> subchannelMap.get(e))
                .forEach(Subchannel::shutdown);

        subchannelMap = newSubchannelMap;
    }

    private CreateSubchannelArgs buildCreateSubchannelArgs(EquivalentAddressGroup e) {
        return CreateSubchannelArgs.newBuilder()
                .setAddresses(e)
                .setAttributes(Attributes.newBuilder()
                        .set(STATE_INFO, new CustomWeightRoundRef<>(IDLE))
                        .build())
                .build();
    }

    private Stream<EquivalentAddressGroup> splitAddressCollection(EquivalentAddressGroup equivalentAddressGroup) {
        Attributes attributes = equivalentAddressGroup.getAttributes();
        return equivalentAddressGroup.getAddresses()
                .stream()
                .map(e -> new EquivalentAddressGroup(e, attributes));
    }

    private Subchannel processSubchannel(Subchannel subchannel) {
        if (subchannelMap.containsValue(subchannel)) {
            log.info("{} {} 已经存在", subchannel, subchannel.getAddresses());
            return subchannel;
        }

        subchannel.start(new CustomWeightRoundSubchannelStateListener(this, subchannel, helper));
        subchannel.requestConnection();
        return subchannel;
    }


    @Override
    public void handleNameResolutionError(Status error) {
        log.info("命名解析失败:{}", error);
        helper.updateBalancingState(ConnectivityState.TRANSIENT_FAILURE, new CustomWeightRoundSubchannelPicker(PickResult.withNoResult()));
    }

    @Override
    public void shutdown() {
        subchannelMap.values()
                .stream()
                .peek(s -> log.info("关闭 {} {}", s, s.getAddresses()))
                .forEach(Subchannel::shutdown);
    }

    public Map<EquivalentAddressGroup, Subchannel> getSubchannelMap() {
        return new ConcurrentHashMap<>(this.subchannelMap);
    }
}


