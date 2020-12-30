package com.merrill.code.common;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/10/22 16:28
 * @Description:
 */
public class C {
    public static int c;

    public C() {
        c = 100;
    }

    public int add(D d, E e) {
        D tmp =  new D();
        tmp.setD(c);
        return d.getD() + e.getE() + tmp.getD();
    }
}
