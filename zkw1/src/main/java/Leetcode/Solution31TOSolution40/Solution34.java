package Leetcode.Solution31TOSolution40;

class Solution34 {
    public int[] searchRange(int[] nums, int target) {
        if (nums.length==0||target < nums[0]) {
            return new int[]{-1, -1};
        }
        int left = findleft(nums, target);
        int right = findright(nums, target);

        return new int[]{left, right};
    }

    private int findleft(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = (right + left) / 2;
            if (target > nums[mid]) {
                left = mid + 1;
            } else if (target == nums[mid]) {
                right = mid ;
            } else {
                right = mid-1;
            }
        }
        if (nums[left] == target) {
            return left;
        }
        return -1;

    }

    private int findright(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = (right + left+1) / 2;
            if (target > nums[mid]) {
                left = mid + 1;
            } else if (target == nums[mid]) {
                left = mid ;
            } else {
                right = mid-1;
            }
        }
        if (nums[right] == target) {
            return right;
        }
        return -1;

    }
}