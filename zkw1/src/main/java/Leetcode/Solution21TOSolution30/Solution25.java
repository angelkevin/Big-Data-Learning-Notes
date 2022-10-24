package Leetcode.Solution21TOSolution30;

import Leetcode.ListNode;

public class Solution25 {

    public ListNode re(ListNode head) {
        ListNode pre = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode tmp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = tmp;
        }
        return pre;

    }

    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode start = dummy;
        ListNode end = dummy;
        while (true) {
            for (int i = 0; (i < k) && end != null; i++) {
                end = end.next;
            }
            if (end == null) {
                break;
            }
            ListNode startNext = start.next;
            ListNode endtNext = end.next;
            end.next = null;
            start.next = re(start.next);
            startNext.next = endtNext;
            start = end = startNext;
        }

        return dummy.next;
    }
}
