package Leetcode;

public class Solutionoffer {
    public static int fib(int n) {
        if(n<=1){
            return n;
        }
        int[] dp = new int[2];
        dp[1]=1;
        int sum=0;

        for (int i = 2; i <= n; i++) {
            sum = (dp[0]+dp[1])%1000000007;
            dp[0]=dp[1];
            dp[1]=sum;
        }
        return sum;
    }
    public static void main(String[] args) {
        System.out.println(fib(45));
    }
}
