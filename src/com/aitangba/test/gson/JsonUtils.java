package com.aitangba.test.gson;

import com.aitangba.test.gson.custom.ReflectiveTypeAdapterFactory;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fhf11991 on 2017/3/22.
 */
public class JsonUtils {

    public static Gson buildGson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(int.class, new IntTypeAdapter())
                .registerTypeAdapter(Integer.class, new IntTypeAdapter())
                .registerTypeAdapter(double.class, new DoubleTypeAdapter())
                .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                .registerTypeAdapter(float.class, new FloatTypeAdapter())
                .registerTypeAdapter(Float.class, new FloatTypeAdapter())
                .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
                .registerTypeAdapter(Boolean.class, new BooleanTypeAdapter())
                .registerTypeHierarchyAdapter(List.class, new ListJsonDeserializer())
                .create();
        ReflectiveTypeAdapterFactory.init(gson);
        return gson;
    }

    public static String toJson(Object src) {
        Gson gson = buildGson();
        return gson.toJson(src);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = buildGson();
        return gson.fromJson(json, classOfT);
    }

    public static <T> HttpResponse<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(HttpResponse.class, new Class[]{clazz});
        return buildGson().fromJson(json, type);
    }

    public static <T> HttpResponse<List<T>> fromJsonArray(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(HttpResponse.class, new Type[]{listType});
        return buildGson().fromJson(json, type);
    }

    public static class HttpResponse<T> {
        public int code;
        public String msg;
        public T child;
    }

    private static class IntTypeAdapter extends TypeAdapter<Integer> {

        @Override
        public Integer read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            String stringValue = reader.nextString();
            try {
                Integer value = Integer.valueOf(stringValue);
                return value;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public void write(JsonWriter writer, Integer value) throws IOException {
            if (value == null) {
                writer.nullValue();
            } else {
                writer.value(value);
            }
        }
    }

    private static class DoubleTypeAdapter extends TypeAdapter<Double> {

        @Override
        public Double read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            String stringValue = reader.nextString();
            try {
                Double value = Double.valueOf(stringValue);
                return value;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public void write(JsonWriter writer, Double value) throws IOException {
            if (value == null) {
                writer.nullValue();
            } else {
                writer.value(value);
            }
        }
    }

    private static class FloatTypeAdapter extends TypeAdapter<Float> {

        @Override
        public Float read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            String stringValue = reader.nextString();
            try {
                Float value = Float.valueOf(stringValue);
                return value;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public void write(JsonWriter writer, Float value) throws IOException {
            if (value == null) {
                writer.nullValue();
            } else {
                writer.value(value);
            }
        }
    }

    private static class BooleanTypeAdapter extends TypeAdapter<Boolean> {

        @Override
        public Boolean read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }

            String stringValue = reader.nextString();
            return Boolean.valueOf(stringValue);
        }

        @Override
        public void write(JsonWriter writer, Boolean value) throws IOException {
            if (value == null) {
                writer.nullValue();
            } else {
                writer.value(value);
            }
        }
    }

    private static class ListJsonDeserializer implements JsonDeserializer<List<?>> {

        @Override
        public List<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                Type itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
                List list = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    JsonElement element = array.get(i);
                    Object item = context.deserialize(element, itemType);
                    list.add(item);
                }
                return list;
            } else {
                return Collections.EMPTY_LIST;
            }
        }
    }
}
