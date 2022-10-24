package Leetcode.Solution21TOSolution30;
/*
kmp算法
获取前缀数组next
然后比较
 */
public class Solution28 {


    public int strStr(String haystack, String needle) {

        int j = 0;
        int[] next = getNext(needle);
        for (int i = 0; i < haystack.length(); i++) {
            while (haystack.charAt(i) != needle.charAt(j) && j > 0) {
                j = next[j - 1];
            }
            if (haystack.charAt(i) == needle.charAt(j)) {
                j++;
            }
            if (j == next.length ) {
                return i - next.length + 1;
            }
        }
        return -1;

    }

    public static int[] getNext(String s) {
        int[] next = new int[s.length()];
        int j = 0;
        for (int i = 1; i < s.length(); i++) {
            while (j > 0 && s.charAt(j) != s.charAt(i)) {
                j = next[j - 1];
            }
            if (s.charAt(i) == s.charAt(j)) {
                j++;
            }
            next[i] = j;

        }
        return next;
    }

}
