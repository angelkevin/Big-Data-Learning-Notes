package Leetcode;

public class Solution344 {
    public void reverseString(char[] s) {
        int right = 0;
        int left = s.length-1;
        while (left>right){
            char tmp = s[left];
            s[left] = s[right];
            s[right] = tmp;
            left--;
            right++;
        }
    }
}
