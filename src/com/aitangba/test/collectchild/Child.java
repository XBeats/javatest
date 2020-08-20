package com.aitangba.test.collectchild;

import org.reflections.Reflections;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by fhf11991 on 2020/8/19.
 */
public class Child implements Parent {
    @Override
    public void say() {
        System.out.println("hello from child ");
    }

    public static void main(String[] args) {
//        File rootFile = new File(Parent.class.getResource("/").getFile().replaceFirst("/", ""));
//        setSonList(rootFile, rootFile.getPath() + "\\", Parent.class);
//
        Reflections reflections = new Reflections("com.aitangba.test.collectchild");
//        Set<Class<? extends Parent>> subTypes = reflections.getSubTypesOf(Parent.class);
//
//        for(Class cls : subTypes) {
//            System.out.println(cls.getName());
//        }

        String path = "com.aitangba.test.collectchild";
        String resourceName = resourceName(path);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        List<URL> result = new ArrayList<>();
        try {
            Enumeration urls = classLoader.getResources(resourceName);
            while (urls.hasMoreElements()) {
                URL url = (URL) urls.nextElement();
                int index = url.toExternalForm().lastIndexOf(resourceName);
                if (index != -1) {
                    result.add(new URL(url, url.toExternalForm().substring(0, index)));
                } else {
                    result.add(url);
                }
            }
        } catch (IOException var11) {
            System.out.println("error getting resources for ");
        }

        List<File> files = new ArrayList<>();
        String tempPath;
        File tempFile;
        for (URL item : result) {
            try {
                tempPath = item.toURI().getSchemeSpecificPart();
                if ((tempFile = new java.io.File(tempPath)).exists()) {
                    files.add(tempFile);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        for (File cls : files) {
            System.out.println("path = " + cls.getAbsolutePath());
        }

        final String childPath = "com.aitangba.test.collectchild.Child".replace(".", "/") + ".class";
        read(childPath);
//        readCustom(path);
    }

    private static String resourceName(String name) {
        if (name != null) {
            String resourceName = name.replace(".", "/");
            resourceName = resourceName.replace("\\", "/");
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }

            return resourceName;
        } else {
            return null;
        }
    }


    private static void read(String path) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource(path);

        try {
            BufferedInputStream fin = new BufferedInputStream(new FileInputStream(resource.getFile()));
            javassist.bytecode.ClassFile classFile = new javassist.bytecode.ClassFile(new DataInputStream(fin));

            System.out.println("name = " + classFile.getName());
            System.out.println("parent name = " + classFile.getSuperclass());
            System.out.println("getInterfaces = " + Arrays.toString(classFile.getInterfaces()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readCustom(String path) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource(path);

        try {
            BufferedInputStream fin = new BufferedInputStream(new FileInputStream(resource.getFile()));
            ClassFile classFile = new ClassFile(new DataInputStream(fin));
            System.out.println("name = " + classFile.getName());
            System.out.println("parent name = " + classFile.getSuperclass());
            System.out.println("getInterfaces = " + Arrays.toString(classFile.getInterfaces()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void setSonList(File rootFile, String parentDirectory, Class<T> parentClass) {
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                setSonList(file, parentDirectory, parentClass);
            }
        } else {
            String className = null;
            try {
                if (rootFile.getPath().indexOf(".class") != -1) {
                    className = rootFile.getPath().replace(parentDirectory, "").replace(".class", "").replace("\\", ".");
                    Class<?> classObject = Class.forName(className);
                    classObject.asSubclass(parentClass);
                    System.out.println(className + " 是 " + parentClass + " 的子类");
                }
            } catch (ClassNotFoundException e) {
                System.err.println("找不到类 " + className);
            } catch (ClassCastException e) {
                System.err.println(className + " 不是 " + parentClass + " 的子类");
            }
        }
    }
}
