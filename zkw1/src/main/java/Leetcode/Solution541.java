package Leetcode;

public class Solution541 {
    public String reverseStr(String s, int k) {
        char[] chars = s.toCharArray();
        for (int i = 0; i < s.length(); i = i + 2 * k) {
            if (s.length() - i >= k) {
                reverseString(chars, i, i + k);
            } else if (s.length() - i < k) {
                reverseString(chars, i, s.length());
            }
        }
        return new String(chars);
    }

    public void reverseString(char[] chars, int start, int end) {
        int right = start;
        int left = end - 1;
        while (left > right) {
            char tmp = chars[left];
            chars[left] = chars[right];
            chars[right] = tmp;
            left--;
            right++;
        }
    }

}
