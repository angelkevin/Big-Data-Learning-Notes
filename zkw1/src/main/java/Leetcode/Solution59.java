package Leetcode;

public class Solution59 {

    public int[][] generateMatrix(int n) {

        int[][] result = new int[n][n];
        int start_x = 0;
        int start_y = 0;
        int loop = n / 2;
        int t = n - 1;
        int mid = n / 2;
        int count = 1;
        while (loop >= 0) {
            int x = start_x;
            int y = start_y;
            for (; y < t; y++) {
                result[start_x][y] = count++;
            }
            for (; x < t; x++) {
                result[x][y] = count++;
            }
            for (; y > start_x; y--) {
                result[x][y] = count++;
            }
            for (; x > start_x; x--) {
                result[x][start_y] = count++;
            }
            if (n % 2 != 0) {
                result[mid][mid] = count;
            }
            start_x++;
            start_y++;
            loop--;
            t = t - 1;
        }
        return result;
    }
}


