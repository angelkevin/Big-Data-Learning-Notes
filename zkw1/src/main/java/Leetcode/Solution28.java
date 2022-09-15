package Leetcode;

public class Solution28 {

    public int strStr(String haystack, String needle) {
        int i = 0;
        int j = i + needle.length();

        while (j <= haystack.length()) {
            System.out.println(haystack.substring(i, j));
            if (haystack.substring(i, j).equals(needle)) {
                return i;
            }
            i++;
            j++;
        }
        return -1;
    }


}
