package com.zkw;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        MyThread myThread = new MyThread();
        myThread.start();
        Thread.sleep(20L);
        for (int i = 0; i < 2000; i++) {
            System.out.println(i);

        }
    }
}
