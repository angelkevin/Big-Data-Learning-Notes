package Leetcode.Solution41TOSolution50;

import java.util.Stack;

/*
我是傻逼
 */
public class Solution42 {
    public int trap(int[] height) {
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        for (int i = 0; i < height.length; i++) {
            while (!stack.empty() && height[stack.peek()] < height[i]) {
                int mid = stack.pop();
                if (stack.empty()) {
                    break;
                }


                int w = i - stack.peek() - 1;
                int h = Math.min(height[stack.peek()], height[i]) - height[mid];

                result += h * w;


            }
            stack.push(i);

        }
        return result;
    }

}

