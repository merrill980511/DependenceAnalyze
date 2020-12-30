package com.merrill.code.inherit;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/11/2 9:15
 * @Description:
 */
public class Main {
    /*
    621009875	856419764
621009875	856419764
621009875	856419764
     */
    public static void main(String[] args) {
//        A a1 = new A();
//        A a2 = new B();
//        A a3 = new C();
//        a1.setA(1);
//        a2.setA(2);
//        a3.setA(3);
//        System.out.println(a1.getA());
//        System.out.println(a2.getA());
//        System.out.println(a3.getA());

        C c = new C();
        c.setA(1);
        c.setB(2);
        //621009875	856419764
        System.out.println(c.getA());
        System.out.println(c.getA());
        System.out.println(c.getA());
        System.out.println(c.getB());
    }
}
