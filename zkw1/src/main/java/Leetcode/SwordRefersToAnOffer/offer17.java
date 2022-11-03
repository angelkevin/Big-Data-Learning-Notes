package Leetcode.SwordRefersToAnOffer;

public class offer17 {
    public int[] printNumbers(int n) {
        int count = (int) Math.pow(10,n);
        int[] ints = new int[count-1];
        for (int i = 0; i < count-1; i++) {
            ints[i]=i+1;

        }
        return ints;

    }
}
