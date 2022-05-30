package com.example;

import java.lang.reflect.Method;

import com.example.annotation.GetterName;

public class Main {
    public static void main(String[] args) {
        try {
            Method method = User.class.getDeclaredMethod("getAge", new Class<?>[] {});
            GetterName getterName = method.getAnnotation(GetterName.class);
            System.out.println("found method annotation value: " + getterName.value());
        } catch (Exception e) {
            System.out.println("can not found method `getAge`");
        }

        try {
            com.sun.tools.javac.Main.main(new String[] {
                    "-cp",
                    "target/classes;${lombok path}lombok-1.18.24.jar;${lombok path}lombok-1.18.24-javadoc.jar;${lombok path}lombok-1.18.24-sources.jar",
                    "-processor",
                    "com.example.AnnoProc,lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
                    "src/main/java/com/example/User.java",
                    "-d",
                    "target/classes"
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
