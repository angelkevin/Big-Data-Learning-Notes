package com.zkw;

public class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 2000; i++) {
            System.out.println("run" + i);

        }
    }
}
