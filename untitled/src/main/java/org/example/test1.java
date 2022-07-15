package org.example;



public class test1 {
    public static void main(String[] args) {
        Student student = new Student();
        student.setAge(10);
        student.setName("phb");
        System.out.println(student.getAge()+"\n"+ student.getName());
        System.out.println(student.toString());
    }

}
