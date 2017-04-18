package com.aitangba.test.gson;

/**
 * Created by fhf11991 on 2017/3/20.
 */
public class MainTest {

    public static void main(String[] args) {
        final String json = "{\"name\":\"sss\", \"child\": {\"age\":\"ss\"}}";

        JsonUtils.HttpResponse<Child> person = JsonUtils.fromJsonObject(json, Child.class);
        System.out.println(JsonUtils.toJson(person));
    }

    private static class Child {

        public String name;

        public Integer age;

    }
}
