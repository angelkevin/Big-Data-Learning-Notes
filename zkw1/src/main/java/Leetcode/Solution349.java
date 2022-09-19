package Leetcode;

import java.util.HashSet;

public class Solution349 {
    public int[] intersection(int[] nums1, int[] nums2) {
        HashSet<Integer> s1 = new HashSet<Integer>();
        HashSet<Integer> s2 = new HashSet<Integer>();
        HashSet<Integer> result =new HashSet<Integer>();

        for (int i :nums1){
            s1.add(i);
        }
        for (int j :nums2){
            s2.add(j);
        }
        for (int num :s1){
            if(s2.contains(num)){
                result.add(num);
            }
        }
        int index = 0;
        int[] aaa = new int[result.size()];
        for(int i :result){
            aaa[index++] = i;
        }
        return aaa;
    }

}
