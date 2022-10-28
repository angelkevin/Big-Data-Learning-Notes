package Leetcode;

import java.util.ArrayList;
import java.util.List;

public class Solution39 {
    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        back(candidates, target, result, list, 0, 0);
        return result;
    }


    public static void back(int[] candidates, int target, List<List<Integer>> result, List<Integer> list, int start, int sum) {
        if (sum == target) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (sum > target) {
                continue;
            }
            sum = sum + candidates[i];
//            System.out.println(sum);
            list.add(candidates[i]);
            back(candidates, target, result, list, i , sum);
            sum = sum - candidates[i];
            list.remove(list.size() - 1);
        }


    }

    public static void main(String[] args) {
        System.out.println(combinationSum(new int[]{8}, 2));
    }


}
