package Leetcode;

import java.util.HashMap;

public class Solution454 {
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int a :nums1){
            for (int b :nums2){
                if (map.containsKey(a+b)){
                    map.put(a+b,map.get(a+b)+1);
                }else {
                    map.put(a+b,1);
                }
            }
        }
        int result=0;
        for (int c :nums3){
            for (int d:nums4){
                if (map.containsKey(-(d + c))){
                    result = result+map.get(-(d + c));
                }
            }
        }
        System.out.println(map);
        return result;


    }
}
