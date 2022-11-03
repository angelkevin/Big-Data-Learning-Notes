package Leetcode.SwordRefersToAnOffer;

import Leetcode.ListNode;

public class offer24 {
    public ListNode reverseList(ListNode head) {
        if (head==null||head.next==null){
            return head;
        }
        ListNode listNode = reverseList(head.next);
        System.out.println(listNode.val);
        head.next.next=head;
        head.next=null;
        return listNode;
    }
}
