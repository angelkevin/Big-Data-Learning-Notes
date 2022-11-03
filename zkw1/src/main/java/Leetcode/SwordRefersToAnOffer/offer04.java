package Leetcode.SwordRefersToAnOffer;

public class offer04 {
    public boolean findNumberIn2DArray(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length < 1)
            return false;
        int i = matrix[0].length;
        for (int[] ints : matrix) {
            if (target <= ints[i - 1]) {
                for (int l = 0; l < i; l++) {
                    if (target == ints[l]) {
                        return true;
                    }

                }
            } else {
                continue;
            }

        }
        return false;
    }
}
