package Leetcode;

public class Solution209{
    public int minSubArrayLen(int target, int[] nums) {

        int i = 0;
        int sum =0;
        int result = 999999;
        for (int j = 0; j < nums.length; j++) {
            sum = sum+nums[j];
            while (sum>=target){
                result = Math.min((j-i+1),result);
                //System.out.println((j-i+1));
                sum = sum-nums[i];
                i++;
            }

        }
        if (result != 999999){
            return result;
        }else{
            return 0;
        }


    }
}
