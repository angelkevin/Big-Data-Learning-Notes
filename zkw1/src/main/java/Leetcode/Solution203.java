package Leetcode;

public class Solution203 {
    public ListNode removeElements(ListNode head, int val) {

        ListNode dummyHead = new ListNode(0);
        dummyHead.next = head;
        ListNode cur = dummyHead;
        while (cur.next != null) {
            if (cur.next.val == val){
                cur.next=cur.next.next;
            }else{
                cur = cur.next;
            }

        }
        return dummyHead.next;

    }
}
