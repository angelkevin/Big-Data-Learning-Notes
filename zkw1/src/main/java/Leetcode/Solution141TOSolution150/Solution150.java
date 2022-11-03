package Leetcode.Solution141TOSolution150;

import java.util.Stack;

public class Solution150 {
    public int evalRPN(String[] tokens) {
        int num1;
        int num2;
        int result = 0;
        Stack<String> num = new Stack<>();
        for (String token : tokens) {
            if (token.equals("+")) {
                num1 = Integer.parseInt(num.pop());
                num2 = Integer.parseInt(num.pop());
                result = num2 + num1;
                num.push(String.valueOf(result));

            } else if (token.equals("-")) {
                num1 = Integer.parseInt(num.pop());
                num2 = Integer.parseInt(num.pop());
                result = num2 - num1;
                num.push(String.valueOf(result));

            } else if (token.equals("*")) {
                num1 = Integer.parseInt(num.pop());
                num2 = Integer.parseInt(num.pop());
                result = num2 * num1;
                num.push(String.valueOf(result));

            } else if (token.equals("/")) {
                num1 = Integer.parseInt(num.pop());
                num2 = Integer.parseInt(num.pop());
                result = num2 / num1;
                num.push(String.valueOf(result));

            } else {
                num.push(token);
            }
        }
        //防止只有一个元素,也可以回收资源
        return Integer.parseInt(num.pop());
    }
}
