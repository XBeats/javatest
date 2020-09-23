package com.aitangba.test.gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fhf11991 on 2017/3/20.
 */
public class MainTest implements Serializable {

    public static void main(String[] args) {
        String json = "{\"age\":12}";
        Person person = JsonUtils.fromJson(json, Person.class);
        System.out.println(JsonUtils.toJson(person));
    }

    public static class Person {
        public String name;
        public int age;
        public List<String> child;
    }
}
