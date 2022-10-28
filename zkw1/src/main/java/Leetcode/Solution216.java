package Leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Solution216 {
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> result = new ArrayList<>();
        LinkedList<Integer> path = new LinkedList<>();
        back(k,n,result,path,1,0);
        return result;
    }

    public static void back(int k, int n, List<List<Integer>> result, LinkedList<Integer> path,int start,int num){
        if (path.size()==k&&num==n){
            result.add(new LinkedList<>(path));
            return;
        }
        for (int i = start; i < 10; i++) {
            if (num>n){
                continue;
            }
            num=num+i;
            path.add(i);
            back(k, n, result, path, i+1, num);
            num=num-i;
            path.removeLast();
        }

    }


}
