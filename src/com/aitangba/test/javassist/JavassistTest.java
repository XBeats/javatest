package com.aitangba.test.javassist;


import javassist.*;

import java.io.IOException;

/**
 * Created by fhf11991 on 2020/9/22.
 */
public class JavassistTest {

    public static void main(String[] args) {
        try {
            ClassPool cPool = new ClassPool(true);
            //如果该文件引入了其它类，需要利用类似如下方式声明
            //cPool.importPackage("java.util.List");

            //设置class文件的位置, 多个jar使用appendClassPath（包括android环境）
            cPool.appendClassPath("E:classes.jar");
            cPool.appendClassPath("E:android-4.1.1.4.jar");

            //获取该class对象
            CtClass cClass = cPool.get("com.cmic.sso.sdk.utils.o");

            //获取到对应的方法
            CtMethod cMethod = cClass.getDeclaredMethod("c");

            //更改该方法的内部实现
            //需要注意的是对于参数的引用要以$开始，不能直接输入参数名称（例$0.name）
            cMethod.setBody("{$0.name = $1;}");

            // 单行无需使用{}，多行运算必须使用{}包裹，不然无法识别多行
            cMethod.setBody("{" +
                    "String var1 = \"none\";\n" +
                    "return var1;\n" +
                    "}");

            //替换原有的文件
            cClass.writeFile("E:\\ss");

            System.out.println("=======修改方法完=========");
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
