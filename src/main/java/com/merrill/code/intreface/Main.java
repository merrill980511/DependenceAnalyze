package com.merrill.code.intreface;

/**
 * @Author: merrill
 * @Date: 2020/11/2 9:31
 * @Description:
 */
public class Main {
    /*

621009875	1265094477	856419764
621009875	2125039532	856419764
     */
    public static void main(String[] args) {
        Data data = new Data(1);
        System.out.println(data.hashCode());
        FetchData f1 = new A();
        FetchData f2 = new B();
        System.out.println(f1.getData(data));
        System.out.println(f2.getData(data));
    }
}
