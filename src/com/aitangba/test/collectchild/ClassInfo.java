package com.aitangba.test.collectchild;

/**
 * Created by fhf11991 on 2020/8/20.
 */
public class ClassInfo extends ConstInfo<Integer> {

    private int className;

    public ClassInfo(int className) {
        this.className = className;
    }

    @Override
    Integer getValue() {
        return className;
    }
}
