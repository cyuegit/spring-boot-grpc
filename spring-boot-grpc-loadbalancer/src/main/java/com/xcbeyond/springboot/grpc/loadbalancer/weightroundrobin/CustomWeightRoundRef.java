package com.xcbeyond.springboot.grpc.loadbalancer.weightroundrobin;

/**
 * @ClassName: CustomWeightRoundRef
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:07
 */
final class CustomWeightRoundRef<T> {
    T value;

    CustomWeightRoundRef(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
