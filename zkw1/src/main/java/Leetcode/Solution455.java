package Leetcode;

import java.util.Arrays;

public class Solution455 {
    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int result = 0;
        for (int j : s) {
            if (result<g.length&&g[result] <= j) {
                result++;
            }
        }
        return result;
    }
}
