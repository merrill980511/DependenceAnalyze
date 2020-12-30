package com.merrill.code.hashcode;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/11/3 11:13
 * @Description:
 */
public class Main {
    /*
    621009875
     */
    public static void main(String[] args) {
        A a = new A();
        System.out.println("hashcode: " + a.hashCode());
        a.a = 100;
        System.out.println("a: " + a.a);
    }
}
