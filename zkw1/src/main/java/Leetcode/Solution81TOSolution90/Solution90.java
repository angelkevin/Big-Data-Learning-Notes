package Leetcode.Solution81TOSolution90;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Solution90 {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> path = new LinkedList<>();
        back(nums,result,path,0);
        Arrays.sort(nums);
        return result;

    }


    public static void back(int[] nums,List<List<Integer>> result,LinkedList<Integer> path,int start){

        result.add(new LinkedList<>(path));

        if (start==nums.length){
            return;
        }

        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]){
                continue;
            }
            path.add(nums[i] );
            back(nums, result, path, i+1);
            path.removeLast();
        }

    }


}
