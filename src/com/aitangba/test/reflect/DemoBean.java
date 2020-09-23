package com.aitangba.test.reflect;

import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by fhf11991 on 2018/7/19.
 */
public class DemoBean {
    public static class ResBody {
        public GroupFilterEntity locationFilter = new GroupFilterEntity();
    }

    public static class GroupFilterEntity implements Serializable {
        public String groupName;
        public List<FilterEntity> filterList = new ArrayList<>();
    }

    public static class FilterEntity implements Serializable {
        public List<FilterInfoEntity> filterInfoList = new CopyOnWriteArrayList<>();
    }

    public static class FilterInfoEntity implements Serializable {
        public double lat;
        public double lon;
    }

    public static void main(String[] args) {
        ResBody resBody = new DemoBean.ResBody();
        try {
            convertInLoop(resBody, ResBody.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void convertInLoop(@Nullable Object object, @Nullable Class<?> typeClass) throws IllegalAccessException {
        if (object == null) {
            return;
        }

        if (List.class.isAssignableFrom(typeClass)) {
            List list = (List) object;
            for (Object item : list){
                convertInLoop(item, null);
            }
        } else {
            Field[] fields = typeClass.getFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAccessible()) {
                    convertInLoop(field.get(object), field.getType());
                }
            }
        }

    }
}
