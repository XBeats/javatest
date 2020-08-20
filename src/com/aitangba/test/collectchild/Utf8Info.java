package com.aitangba.test.collectchild;

/**
 * Created by fhf11991 on 2020/8/20.
 */
public class Utf8Info extends ConstInfo<String> {
    private String value;

    public Utf8Info(String value) {
        this.value = value;
    }

    @Override
    String getValue() {
        return value;
    }
}
