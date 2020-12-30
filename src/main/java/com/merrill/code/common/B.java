package com.merrill.code.common;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/10/22 16:28
 * @Description:
 */
public class B {
    private C c;

    public B() {
    }

    public C getC() {
        return c;
    }

    public void setC(C c) {
        this.c = c;
    }

    public int addAll(int dd, int ee) {
        D d = new D();
        E e = new E();
        d.setD(dd);
        e.setE(ee);
        return c.add(d, e) + d.getD();
    }
}
