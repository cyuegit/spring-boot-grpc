package com.xcbeyond.springboot.grpc.loadbalancer.randomrobin;

/**
 * @ClassName: CustomRandomRef
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:07
 */
final class CustomRandomRef<T> {
    T value;

    CustomRandomRef(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
