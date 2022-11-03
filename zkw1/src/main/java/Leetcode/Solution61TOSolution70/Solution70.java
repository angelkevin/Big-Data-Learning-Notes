package Leetcode.Solution61TOSolution70;

public class Solution70 {
    public static int climbStairs(int n) {
        if (n<=2){
            return n;
        }
        int[] dp = new int[2];
        dp[0]=1;
        dp[1]=2;
        int sum = 0;
        for (int i = 2; i <n ; i++) {
            sum = dp[0]+dp[1];
            dp[0]=dp[1];
            dp[1]=sum;
        }
        return sum;
    }
    public static void main(String[] args) {
        System.out.println(climbStairs(4));
    }
}
