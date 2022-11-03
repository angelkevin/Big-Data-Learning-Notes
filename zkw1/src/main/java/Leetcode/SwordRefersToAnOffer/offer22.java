package Leetcode.SwordRefersToAnOffer;

import Leetcode.ListNode;

public class offer22 {
    public ListNode getKthFromEnd(ListNode head, int k) {
        ListNode tmp = new ListNode();
        tmp=head;
        int count =0;
        while (head!=null){
            count++;
            head = head.next;
        }

        for (int i = 0; i < count-k; i++) {
            tmp=tmp.next;

        }
        return tmp;
    }
}
