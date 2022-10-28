package Leetcode;

import java.util.Stack;

public class Solution32 {
    public int longestValidParentheses(String s) {
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        stack.push(-1);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                if (stack.size() == 1) {
                    stack.pop();
                    stack.push(i);

                } else {
                    stack.pop();
                    result = Math.max(result, i - stack.peek());
                }

            }


        }
        return result;


    }
}
