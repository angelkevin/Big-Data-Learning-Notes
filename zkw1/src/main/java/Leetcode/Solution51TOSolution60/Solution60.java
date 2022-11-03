package Leetcode.Solution51TOSolution60;

import java.util.LinkedList;

public class Solution60 {
    public String getPermutation(int n, int k) {
        LinkedList<String> result = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();

        back( n, 1,  sb , result);
        return result.get(k - 1);

    }


    public void back(int n, int i, StringBuilder sb , LinkedList<String> result){
        if(sb.length()==n){
            result.add(sb.toString());
        }
        for (int j = i; j <= n; j++) {

            sb.append(j);
            back(n, i+1, sb, result);
            sb.deleteCharAt(sb.length()-1);
        }
    }




}
