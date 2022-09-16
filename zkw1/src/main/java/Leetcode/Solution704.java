package Leetcode;

public class Solution704 {
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length-1;
        while (right>=left){
            int middle = (right+left)/2;
            if (nums[middle] > target){
                right = middle -1;
            } else if (nums[middle]<target) {
                left = middle +1;
            }else
                return middle;

        }
        return -1;
    }
}
