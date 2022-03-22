package com.xcbeyond.springboot.grpc.loadbalancer.roundrobin;

/**
 * @ClassName: CustomRoundRef
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:07
 */
final class CustomRoundRef<T> {
    T value;

    CustomRoundRef(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
