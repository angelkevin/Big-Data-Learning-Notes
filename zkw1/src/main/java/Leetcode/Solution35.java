package Leetcode;

class Solution35 {
    public int searchInsert(int[] nums, int target) {
        if (target<nums[0]){
            return 0;
        }
        int left = 0;
        int right = nums.length-1;

        while (left<right){
            int mid = (left+right)/2;
            //(mid,right]
            if (target>nums[mid]){
                left = mid+1;
            }else if(target==nums[mid]){
                return mid;
            }
            //[left,mid)
            else {
                right = mid-1;
            }
        }
        return left;

    }

}