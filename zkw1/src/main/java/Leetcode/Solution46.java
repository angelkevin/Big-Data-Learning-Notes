package Leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Solution46 {
    List<List<Integer>> result = new ArrayList<>();
    LinkedList<Integer> path = new LinkedList<>();

    public List<List<Integer>> permute(int[] nums) {
        if(nums.length==0){
            return result;
        }
        dfs(nums,path);
        return result;
    }

    void dfs(int[] nums, LinkedList<Integer> path) {
        if ( path.size()==nums.length) {
            result.add(new ArrayList<>(path));
            return;

        }
        for (int i = 0; i < nums.length; i++) {
            if (path.contains(nums[i])) {
                continue;
            }
            path.add(nums[i]);
            dfs(nums,path);
            System.out.println("6666");
            path.removeLast();

        }

    }

    public static void main(String[] args) {
        Solution46 solution46 = new Solution46();
        System.out.println(solution46.permute(new int[]{1, 2, 3}));
    }
}
