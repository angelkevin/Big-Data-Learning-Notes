package org.example;

import junit.framework.TestCase;

public class test1Test extends TestCase {
    public static void main(String[] args) {
        Student student = new Student();
        student.setName("张凯文");
        student.setAge(21);
        String zkw = student.toString();
        System.out.println(zkw);
    }

}