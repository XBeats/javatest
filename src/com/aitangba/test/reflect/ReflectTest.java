package com.aitangba.test.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fhf11991 on 2019/2/15.
 */
public class ReflectTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final int size = 100000;
        List<ReflectTest> list = new ArrayList<>();
        ReflectTest reflectTest;
        for (int i = 0; i < size; i++) {
            reflectTest = new ReflectTest();
            reflectTest.name = "name" + i;
            reflectTest.age = "name" + i;
            reflectTest.size1 = "name" + i;
            reflectTest.size2 = "name" + i;
            reflectTest.size3 = "name" + i;
            reflectTest.size4 = "name" + i;
            reflectTest.size5 = "name" + i;
            reflectTest.size6 = "name" + i;
            reflectTest.size7 = "name" + i;
            reflectTest.size8 = "name" + i;
            reflectTest.size9 = "name" + i;
            reflectTest.size10 = "name" + i;
            list.add(reflectTest);
        }

        long originTime = System.currentTimeMillis();

        Field[] itemClassFields = null;
        Object item;
        for (int i = 0; i < size; i++) {
            item = list.get(i);
            if (itemClassFields == null) {
                itemClassFields = item.getClass().getDeclaredFields();
            }
            for (Field field : itemClassFields) {
                System.out.print(getFieldValue(item, field).toString());
            }
        }

        System.out.println();
        System.out.println("1cost time : " + (System.currentTimeMillis() - originTime));
        originTime = System.currentTimeMillis();

//        itemClassFields = null;
//        Method[] methods = null;
//        for (int i = 0; i < size; i++) {
//            item = list.get(i);
//            if (methods == null) {
//                itemClassFields = item.getClass().getDeclaredFields();
//                methods = new Method[itemClassFields.length];
//                int j = 0;
//                for (Field field : itemClassFields) {
//                    String firstLetter = field.getName().substring(0, 1).toUpperCase();
//                    String getter = "get" + firstLetter + field.getName().substring(1);
//                    methods[j] = item.getClass().getMethod(getter, new Class[]{});
//                    j++;
//                }
//            }
//            for (Method method : methods) {
//                System.out.print(method.invoke(item, new Object[]{}).toString());
//            }
//        }
//
//        System.out.println();
//        System.out.println("2cost time : " + (System.currentTimeMillis() - originTime));
    }

    static Object getFieldValue(Object obj, Field field) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (Throwable e) {
            return "";
        }
    }

    public String name;
    public String age;
    public String size1;
    public String size2;
    public String size3;
    public String size4;
    public String size5;
    public String size6;
    public String size7;
    public String size8;
    public String size9;
    public String size10;

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getSize1() {
        return size1;
    }

    public String getSize2() {
        return size2;
    }

    public String getSize3() {
        return size3;
    }

    public String getSize4() {
        return size4;
    }

    public String getSize5() {
        return size5;
    }

    public String getSize6() {
        return size6;
    }

    public String getSize7() {
        return size7;
    }

    public String getSize8() {
        return size8;
    }

    public String getSize9() {
        return size9;
    }

    public String getSize10() {
        return size10;
    }
}
