package Leetcode;

class Solution55 {
    public boolean canJump(int[] nums) {
        int i = 0;
        int len = 0;
        while (i <= len) {
            if (i + nums[i] >= nums.length - 1) {
                return true;
            } else if
            (i + nums[i] > len) {
                len = i + nums[i];

            }

            i++;
        }
        return false;
    }
}