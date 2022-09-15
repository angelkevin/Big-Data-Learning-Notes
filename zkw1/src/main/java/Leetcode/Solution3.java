package Leetcode;

import java.util.HashSet;
import java.util.Set;

public class Solution3 {
    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int left = 0;
        int right = 0;
        int max = 0;
        while (right < s.length()) {
            if (!set.contains(s.charAt(right))) {
                set.add(s.charAt(right));
//                System.out.println(right);
                right++;
            } else {
                set.remove(s.charAt(left));
                System.out.println(left);
                left++;
            }
            max = Math.max(max, set.size());
        }
        return max;
    }


    public static void main(String[] args) {
        Solution3 solution3 = new Solution3();
        solution3.lengthOfLongestSubstring("abcabcbb");

    }
}

