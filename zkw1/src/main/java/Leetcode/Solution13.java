package Leetcode;

class Solution13 {
    private int getValue(char ch) {
        switch(ch) {
            case 'I': return 1;
            case 'V': return 5;
            case 'X': return 10;
            case 'L': return 50;
            case 'C': return 100;
            case 'D': return 500;
            case 'M': return 1000;
            default: return 0;
        }
    }

    public int romanToInt(String s) {
        int n=s.length();
        int ans=0;
        int i=0;
        for(;i<n-1;i++){
            int a=getValue(s.charAt(i));
            int b=getValue(s.charAt(i+1));
            if(a<b){
                ans-=a;
            }else{
                ans+=a;
            }
        }
        ans+=getValue(s.charAt(i));
        return ans;
    }
}