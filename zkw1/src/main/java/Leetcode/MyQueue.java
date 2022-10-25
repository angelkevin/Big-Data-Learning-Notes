package Leetcode;

import java.util.Stack;

class MyQueue {
    Stack<Integer> in;
    Stack<Integer> out;
    public MyQueue() {
        in = new Stack<Integer>();
        out = new Stack<Integer>();

    }

    public void push(int x) {
        in.push(x);
    }

    public int pop() {
        if (out.empty()){
            while (!in.empty()){
                out.push(in.pop());
            }
        }
        int result = 0;
        result = out.pop();
        return result;
    }

    public int peek() {
        if (out.empty()){
            while (!in.empty()){
                out.push(in.pop());
            }
        }
        return out.peek();
    }

    public boolean empty() {
        return in.empty()&&out.empty();
    }
}

/**
 * Your MyQueue object will be instantiated and called as such:
 * MyQueue obj = new MyQueue();
 * obj.push(x);
 * int param_2 = obj.pop();
 * int param_3 = obj.peek();
 * boolean param_4 = obj.empty();
 */