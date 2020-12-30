package com.merrill.code.intreface;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/11/2 9:29
 * @Description:
 */
public class A implements FetchData {
    @Override
    public int getData(Data data) {
        return data.getData();
    }
}
