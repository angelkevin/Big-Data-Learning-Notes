package Leetcode.Solution11TOSolution20;

import Leetcode.ListNode;

public class Solution19 {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode result = head;
        ListNode res = head;
        for (int i = 0; i < n; i++) {
            result = result.next;
            System.out.println(res.val);
        }
        if (result == null) {
            return head.next;
        }
        while (result.next != null) {
            result = result.next;
            res = res.next;
        }
        res.next = res.next.next;
        return head;
    }
}
