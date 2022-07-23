package Leetcode;

import java.util.*;

public class Solution16 {
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int result = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < nums.length; i++) {
            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[right] + nums[left];
                if (Math.abs(result - target) > Math.abs(sum - target)) {
                    result = sum;

                }
                if (sum > target) {
                    right--;

                } else if (sum < target) {
                    left++;
                } else
                    return target;

            }
        }
        return result;
    }
}
