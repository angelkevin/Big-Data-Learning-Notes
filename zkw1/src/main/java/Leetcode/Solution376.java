package Leetcode;

public class Solution376 {
    public int wiggleMaxLength(int[] nums) {
        if(nums.length==1){
            return 1;
        }
        int cur =0;
        int per=0;
        int result =1;
        for (int i = 0; i < nums.length-1; i++) {
            cur = nums[i+1]-nums[i];
            if (cur>0&&per<=0||(per>=0&&cur<0)){
                result++;
                per=cur;
            }

        }
        return result;
    }
}
