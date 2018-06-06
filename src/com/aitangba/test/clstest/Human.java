package com.aitangba.test.clstest;

public class Human {

    public static void say() {
        new Child() {
            @Override
            public void say() {
                super.say();
            }
        }.say();
    }

    private static class Child {
        public void say() {

        }
    }
}
