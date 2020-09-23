package com.aitangba.test.gson.custom;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.*;
import com.google.gson.internal.reflect.ReflectionAccessor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

/**
 * Type adapter that reflects over the fields and methods of a class.
 */
public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
    private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();

    private ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor,
                                         FieldNamingStrategy fieldNamingPolicy,
                                         Excluder excluder,
                                         JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
        this.jsonAdapterFactory = jsonAdapterFactory;
    }

    public static void init(Gson gson) {
        try {
            Field field = Gson.class.getDeclaredField("factories");
            field.setAccessible(true);
            List<TypeAdapterFactory> originFactories = (List<TypeAdapterFactory>) field.get(gson);
            List<TypeAdapterFactory> factories = new ArrayList<>(originFactories);
            int index = -1;
            for (TypeAdapterFactory item : factories) {
                index++;
                if (item instanceof com.google.gson.internal.bind.ReflectiveTypeAdapterFactory) {
                    break;
                }
            }

            if (index >= 0) {
                Field constructorField = Gson.class.getDeclaredField("constructorConstructor");

                constructorField.setAccessible(true);
                ConstructorConstructor constructor = (ConstructorConstructor) constructorField.get(gson);

                JsonAdapterAnnotationTypeAdapterFactory factory = new JsonAdapterAnnotationTypeAdapterFactory(constructor);

                ReflectiveTypeAdapterFactory reflectiveTypeAdapterFactory = new ReflectiveTypeAdapterFactory(constructor, gson.fieldNamingStrategy(), gson.excluder(), factory);
                factories.add(index, reflectiveTypeAdapterFactory);

                field.set(gson, Collections.unmodifiableList(factories));
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        // check args
        if (constructorConstructor == null || fieldNamingPolicy == null || excluder == null || jsonAdapterFactory == null) {
            return null;
        }

        Class<? super T> raw = type.getRawType();

        if (!Object.class.isAssignableFrom(raw)) {
            return null; // it's a primitive!
        }

        ObjectConstructor<T> constructor = constructorConstructor.get(type);
        HashSet<BoundField> initMap = new HashSet<>();
        Map<String, BoundField> boundFields = getBoundFields(gson, type, raw, initMap);
        return new Adapter<T>(constructor, boundFields, initMap);
    }

    private boolean excludeField(Field f, boolean serialize) {
        return excludeField(f, serialize, excluder);
    }

    static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
        return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
    }

    /**
     * first element holds the default name
     */
    private List<String> getFieldNames(Field f) {
        SerializedName annotation = f.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = fieldNamingPolicy.translateName(f);
            return Collections.singletonList(name);
        }

        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }

        List<String> fieldNames = new ArrayList<String>(alternates.length + 1);
        fieldNames.add(serializedName);
        for (String alternate : alternates) {
            fieldNames.add(alternate);
        }
        return fieldNames;
    }

    private BoundField createBoundField(
            final Gson context, final Field field, final String name,
            final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        // special casing primitives here saves ~5% on Android...
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        TypeAdapter<?> mapped = null;
        if (annotation != null) {
            mapped = jsonAdapterFactory.getTypeAdapter(
                    constructorConstructor, context, fieldType, annotation);
        }
        final boolean jsonAdapterPresent = mapped != null;
        if (mapped == null) mapped = context.getAdapter(fieldType);

        final TypeAdapter<?> typeAdapter = mapped;
        return new BoundField(name, serialize, deserialize) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            // the type adapter and field type always agree
            @Override
            void write(JsonWriter writer, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = field.get(value);
                TypeAdapter t = jsonAdapterPresent ? typeAdapter
                        : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
                t.write(writer, fieldValue);
            }

            @Override
            void read(JsonReader reader, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
            }

            @Override
            void initField(Object value) throws IOException, IllegalAccessException {
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                if (fieldValue == null) {
                    if (fieldType.getRawType() == String.class) {
                        field.set(value, "");
                    } else if (isArrayType(fieldType)) {
                        Type type = fieldType.getType();
                        Type componentType = $Gson$Types.getArrayComponentType(type);
                        Class rawType = $Gson$Types.getRawType(componentType);
                        field.set(value, Array.newInstance(rawType, 0));
                    } else if (isCollectionType(fieldType)) {
                        field.set(value, new ArrayList());
                    }
                }
            }

            @Override
            public boolean writeField(Object value) throws IOException, IllegalAccessException {
                if (!serialized) return false;
                Object fieldValue = field.get(value);
                return fieldValue != value; // avoid recursion for example for Throwable.cause
            }
        };
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw, HashSet<BoundField> initMap) {
        Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }

        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = excludeField(field, true);
                boolean deserialize = excludeField(field, false);
                if (!serialize && !deserialize) {
                    continue;
                }
                accessor.makeAccessible(field);
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = getFieldNames(field);
                BoundField previous = null;
                for (int i = 0, size = fieldNames.size(); i < size; ++i) {
                    String name = fieldNames.get(i);
                    TypeToken<?> typeToken = TypeToken.get(fieldType);
                    if (i != 0) serialize = false; // only serialize the default name
                    BoundField boundField = createBoundField(context, field, name,
                            typeToken, serialize, deserialize);
                    BoundField replaced = result.put(name, boundField);
                    // append first field, intercept String.class and List.class
                    if (i == 0) {
                        if (fieldType == String.class || isArrayType(typeToken) || isCollectionType(typeToken)) {
                            initMap.add(boundField);
                        }
                    }
                    if (previous == null) previous = replaced;
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType
                            + " declares multiple JSON fields named " + previous.name);
                }
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    private static boolean isArrayType(TypeToken<?> typeToken) {
        Type type = typeToken.getType();
        return type instanceof GenericArrayType || type instanceof Class && ((Class) type).isArray();
    }

    private static boolean isCollectionType(TypeToken<?> typeToken) {
        return Collection.class.isAssignableFrom(typeToken.getRawType());
    }

    static abstract class BoundField {
        final String name;
        final boolean serialized;
        final boolean deserialized;

        private BoundField(String name, boolean serialized, boolean deserialized) {
            this.name = name;
            this.serialized = serialized;
            this.deserialized = deserialized;
        }

        abstract boolean writeField(Object value) throws IOException, IllegalAccessException;

        abstract void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException;

        abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;

        abstract void initField(Object value) throws IOException, IllegalAccessException;

    }

    public static final class Adapter<T> extends TypeAdapter<T> {
        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;
        private final HashSet<BoundField> initBoundFields;

        Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields, HashSet<BoundField> initBoundFields) {
            this.constructor = constructor;
            this.boundFields = boundFields;
            this.initBoundFields = initBoundFields;
        }

        @Override
        public T read(JsonReader in) throws IOException {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                case BEGIN_ARRAY:
                    in.beginArray();
                    in.endArray();
                    return null;
            }

            T instance = constructor.construct();

            try {
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    BoundField field = boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                    } else {
                        field.read(in, instance);
                    }
                }
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            in.endObject();

            try {
                for (BoundField item : initBoundFields) {
                    item.initField(instance);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return instance;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            try {
                for (BoundField boundField : boundFields.values()) {
                    if (boundField.writeField(value)) {
                        out.name(boundField.name);
                        boundField.write(out, value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
            out.endObject();
        }
    }
}