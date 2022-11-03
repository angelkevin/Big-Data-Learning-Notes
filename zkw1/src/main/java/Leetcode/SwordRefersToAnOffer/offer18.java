package Leetcode.SwordRefersToAnOffer;

import Leetcode.ListNode;

public class offer18 {

    public ListNode deleteNode(ListNode head, int val) {
        ListNode dm = new ListNode();
        ListNode tmp = new ListNode();
        ListNode cur = new ListNode();
        cur.next=head;
        dm.next = head;
        if (dm.next.val==val){
            return dm.next.next;
        }
        while (cur.next.val != val){
            tmp = cur.next.next;
            cur.next = tmp;
        }
        cur.next = cur.next.next;
        return dm.next;

    }
}
