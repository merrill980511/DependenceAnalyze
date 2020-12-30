package com.merrill.code.intreface;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/11/2 9:31
 * @Description:
 */
public class B implements FetchData {
    @Override
    public int getData(Data data) {
        return data.getData();
    }
}
