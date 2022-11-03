package Leetcode.SwordRefersToAnOffer;

public class offer10_2 {
    public int numWays(int n) {

        if (n<=2){
            return n;
        }
        int[] ints = {1, 2};
        int result =0;
        for (int i = 2; i <n ; i++) {
            result = ints[0]+ints[1];
            ints[0]=ints[1];
            ints[1]=result;
        }
        return result;

    }
}
