package Leetcode.Solution1TOSolution10;

public class Solution9 {
    public boolean isPalindrome(int x) {
        String s = String.valueOf(x);
        int right, left;
        if (s.length() % 2 == 0) {
            right = (int) s.length() / 2 - 1;
            left = (int) s.length() / 2;
            while (right >= 0 && left < s.length()) {
                if (s.charAt(right) == s.charAt(left)) {
                    right--;
                    left++;
                } else {
                    return false;

                }
            }
            return true;
        } else {
            right = (int) s.length() / 2;
            left = right;
            while (right >= 0 && left < s.length()) {
                if (s.charAt(right) == s.charAt(left)) {
                    right--;
                    left++;
                } else {
                    return false;

                }
            }return true;
        }

    }
}
