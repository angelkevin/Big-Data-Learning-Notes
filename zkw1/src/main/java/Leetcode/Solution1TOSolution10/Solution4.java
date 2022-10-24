package Leetcode.Solution1TOSolution10;

public class Solution4 {

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] c = new int[nums1.length+nums2.length];
        int i=0;
        int j=0;
        int k=0;
        double result;

        while(i<nums1.length && j<nums2.length) {
            if(nums1[i]<nums2[j]) {
                c[k++] = nums1[i++];
            }else {
                c[k++] = nums2[j++];
            }
        }

        while(i<nums1.length) {
            c[k++] = nums1[i++];
        }


        while(j<nums2.length) {
            c[k++] = nums2[j++];
        }



        if (c.length % 2 == 0) {
            result = (double)(c[c.length / 2] + c[c.length / 2 - 1]) / 2;

        } else {
            result = c[(int) (c.length / 2 )];
        }
        return result;
    }

}
