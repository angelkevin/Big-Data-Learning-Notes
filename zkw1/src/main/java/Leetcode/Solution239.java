package Leetcode;

import java.util.LinkedList;

public class Solution239 {
    public static class myQueue {
        LinkedList<Integer> q = new LinkedList<>();
        void add(int val) {
            while (!q.isEmpty() && val > q.getLast()) {
                q.removeLast();
            }
            q.add(val);
        }

        void poll(int val) {
            if (!q.isEmpty() && val == q.peek()) {
                q.poll();
            }
        }

        int getmax(){
            return q.peek();
        }

    }
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums.length == 1) {
            return nums;
        }
        int len = nums.length - k + 1;

        int[] res = new int[len];
        int num = 0;

        myQueue myQueue = new myQueue();

        for (int i = 0; i < k; i++) {
            myQueue.add(nums[i]);
        }
        res[num++] = myQueue.getmax();
        for (int i = k; i < nums.length; i++) {

            myQueue.poll(nums[i - k]);

            myQueue.add(nums[i]);

            res[num++] = myQueue.getmax();
        }
        return res;
    }

}
