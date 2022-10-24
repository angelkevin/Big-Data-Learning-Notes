package Leetcode.Solution1TOSolution10;

import java.util.HashMap;

public class Solution1 {
    public int[] twoSum(int[] nums, int target) {

        int[] res = new int[2];
        if (nums.length == 1 || nums.length == 0) {
            return res;
        }
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int temp = target - nums[i];
            if (hashMap.containsKey(temp)) {
                res[1] = i;
                res[0] = hashMap.get(temp);
            }
            hashMap.put(nums[i], i);

        }
        return res;

    }

}
