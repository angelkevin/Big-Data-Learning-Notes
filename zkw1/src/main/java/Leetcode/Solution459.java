package Leetcode;

public class Solution459 {
    public boolean repeatedSubstringPattern(String s) {
        int[] next = new int[s.length()];
        int j = 0;
        for (int i = 1; i < s.length(); i++) {
            while (s.charAt(i) != s.charAt(j) && j > 0) {
                j = next[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        if (s.length()==0){
            return false;
        }
        int x = s.length()-next[next.length-1]-1;

        return (next[next.length-1]!=0&&s.length()% x == 0);
    }
}
