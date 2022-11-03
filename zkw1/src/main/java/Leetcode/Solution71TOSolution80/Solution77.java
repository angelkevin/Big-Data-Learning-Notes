package Leetcode.Solution71TOSolution80;

import java.util.ArrayList;
import java.util.List;

public class Solution77 {
    public static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        back(n,k,result,list,1);
        return result;
    }


    public static void back(int n, int k, List<List<Integer>> result, List<Integer> list,int start) {
        if (list.size() == k) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = start; i <= n-(k-list.size())+1;i++) {
            list.add(i);
            back(n,k,result,list,i+1);
            list.remove(list.size()-1);
        }


    }

    public static void main(String[] args) {
        System.out.println(combine(3, 3));
    }

}
