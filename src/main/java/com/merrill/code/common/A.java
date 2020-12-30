package com.merrill.code.common;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/10/22 16:28
 * @Description:
 */
public class A {
    public int a;
    private int aa;

    private B b;

    public A() {
        b = new B();
        b.getC();
        b.setC(new C());
    }

    public int getRes(int dd, int ee) {
        return b.addAll(dd, ee);
    }
}
