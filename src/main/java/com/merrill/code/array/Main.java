package com.merrill.code.array;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/11/2 15:30
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        Data[] data = new Data[4];
        B.data = data;
        B.a = new A();
        B.b = 1;
    }
}

class A{

}

class B{
    public static Data[] data;
    public static A a;
    public static int b;
}

class Data {

}
