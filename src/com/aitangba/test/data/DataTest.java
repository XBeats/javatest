package com.aitangba.test.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fhf11991 on 2017/3/28.
 */
public class DataTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
//        list.add("cvce");
//        list.add("cvceddd");
//        list.add("cvcedddsdsd");

        String listStr = list.toString();
        System.out.println(listStr);
        System.out.println(listStr.substring(1, listStr.length() - 1));
    }



}
