package Leetcode.Solution11TOSolution20;


import java.util.Stack;

public class Solution20 {
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(')');

            } else if (s.charAt(i) == '[') {
                stack.push(']');

            } else if (s.charAt(i) == '{') {
                stack.push('}');
            }else if(stack.empty()||stack.peek()!=s.charAt(i)){
                return false;
            }else {
                stack.pop();
            }

        }
        return stack.empty();

    }

    public static void main(String[] args) {
        System.out.println(isValid("(]"));

    }
}

