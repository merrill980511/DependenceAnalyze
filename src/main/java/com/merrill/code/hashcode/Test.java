package com.merrill.code.hashcode;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/11/3 11:18
 * @Description:
 */
public class Test {
    public static void main(String[] args) {
        A a = new A();
        a.a = 100;
        System.out.println(a.hashCode());
        System.out.println(a.a);
    }
}
