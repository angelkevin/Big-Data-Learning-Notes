package Leetcode.Solution21TOSolution30;

import Leetcode.ListNode;

public class Solution24 {
    public ListNode swapPairs(ListNode head) {
        ListNode d = new ListNode(0);
        d.next = head;
        ListNode c = d;
        while (c.next != null && c.next.next!=null){
            ListNode tmp = c.next;
            ListNode tmp1 = c.next.next.next;
            c.next=c.next.next;
            c.next.next=tmp;
            tmp.next = tmp1;
            c = c.next.next;
        }
        return d.next;
    }
}
