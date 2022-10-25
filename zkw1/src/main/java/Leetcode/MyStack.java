package Leetcode;

import java.util.LinkedList;

public class MyStack {

    LinkedList<Integer> q;


    public MyStack() {
        q = new LinkedList<Integer>();
    }

    public void push(int x) {
        q.push(x);

    }

    public int pop() {
        int size = q.size();
        for (int i = 0; i < size-1; i++) {
            q.push(q.pop());
        }
        return q.pop();
    }

    public int top() {
        int size = q.size();
        for (int i = 0; i < size-1; i++) {
            q.push(q.pop());
        }
        return q.getFirst();

    }

    public boolean empty() {
        return q.isEmpty();
    }
}
/*
队列先进先出
栈先进后出

 */

