package com.aitangba.test.gson;

import java.io.*;

/**
 * Created by fhf11991 on 2020/9/23.
 */
public class SerializableTest {
    public static void main(String[] args) {
        final String json = "{\"name\":\"sss\", \"child\": {\"age\":\"ss\"}}";

        Child child = new Child();
        child.name = "zhangsan";
        child.age = 12;
        child.aClass = JsonUtils.class.getName();
//        JsonUtils.HttpResponse<Child> person = JsonUtils.fromJsonObject(json, Child.class);

        File file = new File("D:\\student.txt");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //序列化持久化对象
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(child);
            out.close();

//            System.out.println(new Gson().toJson(child));
        } catch (Exception e) {

        }
    }

    private static class Child implements Serializable {

        public String name;

        public Integer age;

        public String aClass;
    }
}
