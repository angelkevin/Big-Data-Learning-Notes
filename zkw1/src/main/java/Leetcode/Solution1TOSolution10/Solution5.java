package Leetcode.Solution1TOSolution10;

public class Solution5 {
    public String longestPalindrome(String s) {
        int n = s.length(), res = 0;
        int p1 = 0, p2 = 0;
        for(int i=0; i<n; i++){
            int left = i, right = i;
            while(left >= 0 && right < n){
                if(s.charAt(left) == s.charAt(right)){
                    if(right - left + 1 > res){
                        res = right - left + 1;
                        p1 = left; p2 = right;
                    }
                    left--; right++;
                }else break;
            }
            // System.out.println(left);
        }
        for(int i=0; i<n-1; i++){
            int left = i, right = i+1;
            while(left >= 0 && right < n){
                if(s.charAt(left) == s.charAt(right)){
                    if(right - left + 1 > res){
                        res = right - left + 1;
                        p1 = left; p2 = right;
                    }
                    left--; right++;
                }else break;
            }
            System.out.println(left);
        }
        return s.substring(p1, p2+1);
    }
}
