package com.xcbeyond.springboot.grpc.client.grpc.loadbalancer;

/**
 * @ClassName: Ref
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:07
 */
final class Ref<T> {
    T value;

    Ref(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}