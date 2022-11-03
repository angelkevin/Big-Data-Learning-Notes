package Leetcode.Solution71TOSolution80;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Solution78 {


    public static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> path = new LinkedList<>();
        back(nums,0,result,path);
        return result;
    }


    public static void back(int[] nums,int start,List<List<Integer>> result,LinkedList<Integer> path){
        result.add(new LinkedList<>(path));
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);
            back(nums,i+1,result,path);
            path.removeLast();
        }

    }

}
