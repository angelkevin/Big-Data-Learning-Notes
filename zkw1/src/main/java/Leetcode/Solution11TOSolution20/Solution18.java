package Leetcode.Solution11TOSolution20;

import java.util.*;

public class Solution18 {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        HashSet<List<Integer>> result = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            for (int j = i+1; j < nums.length ; j++) {
                ArrayList<Integer> integers = new ArrayList<>();
                int left = j+1;
                int right = nums.length-1;
                while (right>left){
                    if(nums[i]+nums[j]+nums[right]+nums[left]>target){
                        left--;
                    } else if (nums[i]+nums[j]+nums[right]+nums[left]<target) {
                        right++;
                    }else {
                        integers.add(i);
                        integers.add(j);
                        integers.add(right);
                        integers.add(left);
                    }
                    right--;
                    left++;
                    result.add(integers);
                }

            }

        }
        return new ArrayList<>(result);

    }
}
