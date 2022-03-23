package com.xcbeyond.springboot.grpc.loadbalancer.weightrandomrobin;

/**
 * @ClassName: CustomWeightRandomRef
 * @Description:
 * @Author: chenglong.yue
 * @Date: 2022/3/20 21:07
 */
final class CustomWeightRandomRef<T> {
    T value;

    CustomWeightRandomRef(T value) {
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
