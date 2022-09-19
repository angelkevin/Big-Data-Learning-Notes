package Leetcode;

public class Solution242 {
    public boolean isAnagram(String s, String t) {
        int[] reword = new int[26];
        char a = 'a';
        for (int i = 0; i < s.length(); i++) {
            reword[(int)s.charAt(i)-(int)a] ++;
        }
        for (int i = 0; i < t.length(); i++) {
            reword[(int)t.charAt(i)-(int)a] --;
        }
        for (int num : reword){
            if (num!=0){
                return false;
            }
        }return true;
    }


}
