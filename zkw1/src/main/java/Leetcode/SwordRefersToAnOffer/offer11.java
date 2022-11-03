package Leetcode.SwordRefersToAnOffer;

public class offer11 {
    public int minArray(int[] numbers) {
        int result = numbers[0];

        for (int number : numbers) {
            if (result>number){
                result=number;
            }
        }
        return result;

    }



}
