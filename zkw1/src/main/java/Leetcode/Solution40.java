package Leetcode;

import java.util.*;

public class Solution40 {

    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> path = new LinkedList<>();
        Arrays.sort(candidates);
        back(candidates,target,result,path,0);


        return result;
    }

    public static void back(int[] candidates, int target,List<List<Integer>> result, LinkedList<Integer> path,int start){
        if (target==0){
            result.add(new LinkedList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (candidates[i]>target){
                break;
            }
            else if (i>start&&candidates[i]==candidates[i-1]){
                continue;
            }
            target=target-candidates[i];
            path.add(candidates[i]);
            back(candidates, target, result, path, i+1);
            target=target+candidates[i];
            path.removeLast();
        }

    }

}
