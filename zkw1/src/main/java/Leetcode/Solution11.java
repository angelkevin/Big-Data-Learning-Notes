package Leetcode;

public class Solution11 {
    public int maxArea(int[] height) {
        int right = 0;
        int aera = 0;
        int max = 0;
        int left = height.length - 1;
        for (int i = 0; i < height.length; i++) {
            if (height[right] > height[left]) {
                aera = (left - right) * height[left];
                left--;
            } else {
                aera = (left - right) * height[right];
                right++;
            }
            if (max < aera) {
                max = aera;
            }
        }
        return max;

    }

}
